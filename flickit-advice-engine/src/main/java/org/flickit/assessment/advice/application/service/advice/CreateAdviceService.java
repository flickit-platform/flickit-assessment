package org.flickit.assessment.advice.application.service.advice;

import ai.timefold.solver.core.api.solver.SolverManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.advice.application.domain.Plan;
import org.flickit.assessment.advice.application.domain.Question;
import org.flickit.assessment.advice.application.domain.advice.AdviceListItem;
import org.flickit.assessment.advice.application.exception.FinalSolutionNotFoundException;
import org.flickit.assessment.advice.application.port.in.CreateAdviceUseCase;
import org.flickit.assessment.advice.application.port.out.LoadAdviceCalculationInfoPort;
import org.flickit.assessment.advice.application.port.out.assessment.LoadAssessmentResultPort;
import org.flickit.assessment.advice.application.port.out.assessment.LoadAssessmentSpacePort;
import org.flickit.assessment.advice.application.port.out.question.LoadCreatedAdviceDetailsPort;
import org.flickit.assessment.advice.application.port.out.space.CheckSpaceAccessPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.CalculateNotValidException;
import org.flickit.assessment.common.exception.ConfidenceCalculationNotValidException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.flickit.assessment.advice.common.ErrorMessageKey.*;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CreateAdviceService implements CreateAdviceUseCase {

    private final LoadAssessmentSpacePort loadAssessmentSpacePort;
    private final CheckSpaceAccessPort checkSpaceAccessPort;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadAdviceCalculationInfoPort loadAdviceCalculationInfoPort;
    private final SolverManager<Plan, UUID> solverManager;
    private final LoadCreatedAdviceDetailsPort loadCreatedAdviceDetailsPort;

    @Override
    public Result createAdvice(Param param) {
        validateUserAccess(param.getAssessmentId(), param.getCurrentUserId());

        validateCalculatedAssessmentResult(param.getAssessmentId());

        var problem = loadAdviceCalculationInfoPort.loadAdviceCalculationInfo(param.getAssessmentId(), param.getAttributeLevelTargets());
        var solution = solverManager.solve(UUID.randomUUID(), problem);
        Plan plan;
        try {
            plan = solution.getFinalBestSolution();
        } catch (InterruptedException e) {
            log.error("Finding best solution for assessment {} interrupted", param.getAssessmentId(), e.getCause());
            Thread.currentThread().interrupt();
            throw new FinalSolutionNotFoundException(CREATE_ADVICE_FINDING_BEST_SOLUTION_EXCEPTION);
        } catch (ExecutionException e) {
            log.error("Error occurred while calculating best solution for assessment {}", param.getAssessmentId(), e.getCause());
            throw new FinalSolutionNotFoundException(CREATE_ADVICE_FINDING_BEST_SOLUTION_EXCEPTION);
        }
        return mapToResult(plan);
    }

    private void validateUserAccess(UUID assessmentId, UUID currentUserId) {
        var spaceId = loadAssessmentSpacePort.loadAssessmentSpaceId(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_ADVICE_ASSESSMENT_NOT_FOUND));

        if (!checkSpaceAccessPort.checkIsMember(spaceId, currentUserId))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }

    private void validateCalculatedAssessmentResult(UUID assessmentId) {
        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_ADVICE_ASSESSMENT_RESULT_NOT_FOUND));
        if (!Boolean.TRUE.equals(assessmentResult.isConfidenceValid())) {
            log.warn("The calculated confidence value is not valid for [assessmentId={}].", assessmentId);
            throw new ConfidenceCalculationNotValidException(CREATE_ADVICE_ASSESSMENT_RESULT_NOT_VALID);
        }

        if (!Boolean.TRUE.equals(assessmentResult.isCalculateValid())) {
            log.warn("The calculated result is not valid for [assessmentId={}].", assessmentId);
            throw new CalculateNotValidException(CREATE_ADVICE_ASSESSMENT_RESULT_NOT_VALID);
        }
    }

    private Result mapToResult(Plan solution) {
        var questionIdsMap = solution.getQuestions().stream()
            .filter(Question::isRecommended)
            .collect(Collectors.toMap(Question::getId, Function.identity()));

        var adviceQuestionDetails = loadCreatedAdviceDetailsPort.loadAdviceDetails(questionIdsMap.keySet().stream().toList());

        var adviceListItems = adviceQuestionDetails.stream().map(adv -> {
                var question = questionIdsMap.get(adv.question().id());
                var answeredOption = question.getCurrentOptionIndex() != null ? adv.options().get(question.getCurrentOptionIndex()) : null;
                var recommendedOption = adv.options().get(question.getRecommendedOptionIndex());
                var benefit = question.calculateBenefit();

                return new AdviceListItem(
                    adv.question(),
                    answeredOption,
                    recommendedOption,
                    benefit,
                    adv.attributes(),
                    adv.questionnaire());
            }).sorted(Comparator.comparingDouble(AdviceListItem::benefit))
            .toList();

        return new Result(adviceListItems);
    }
}
