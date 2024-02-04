package org.flickit.assessment.advice.application.service.advice;

import ai.timefold.solver.core.api.solver.SolverManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.advice.application.domain.Plan;
import org.flickit.assessment.advice.application.domain.Question;
import org.flickit.assessment.advice.application.domain.advice.AdviceListItem;
import org.flickit.assessment.advice.application.domain.advice.AdviceQuestion;
import org.flickit.assessment.advice.application.exception.FinalSolutionNotFoundException;
import org.flickit.assessment.advice.application.port.in.CreateAdviceUseCase;
import org.flickit.assessment.advice.application.port.out.LoadAdviceCalculationInfoPort;
import org.flickit.assessment.advice.application.port.out.assessment.LoadAssessmentResultValidationFieldsPort;
import org.flickit.assessment.advice.application.port.out.assessment.UserAssessmentAccessibilityPort;
import org.flickit.assessment.advice.application.port.out.question.LoadAdviceImpactfulQuestionsPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.CalculateNotValidException;
import org.flickit.assessment.common.exception.ConfidenceCalculationNotValidException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.flickit.assessment.advice.common.ErrorMessageKey.CREATE_ADVICE_ASSESSMENT_RESULT_NOT_VALID;
import static org.flickit.assessment.advice.common.ErrorMessageKey.CREATE_ADVICE_FINDING_BEST_SOLUTION_EXCEPTION;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CreateAdviceService implements CreateAdviceUseCase {

    private final UserAssessmentAccessibilityPort userAssessmentAccessibilityPort;
    private final LoadAssessmentResultValidationFieldsPort loadAssessmentResultValidationFieldsPort;
    private final LoadAdviceCalculationInfoPort loadAdviceCalculationInfoPort;
    private final SolverManager<Plan, UUID> solverManager;
    private final LoadAdviceImpactfulQuestionsPort loadAdviceImpactfulQuestionsPort;

    @Override
    public Result createAdvice(Param param) {
        validateUserAccess(param.getAssessmentId(), param.getCurrentUserId());

        var validationFields = loadAssessmentResultValidationFieldsPort.loadValidationFields(param.getAssessmentId());
        validateAssessmentResultCalculation(validationFields, param.getAssessmentId());
        validateAssessmentResultConfidence(validationFields, param.getAssessmentId());

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
        if (!userAssessmentAccessibilityPort.hasAccess(assessmentId, currentUserId))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }

    private void validateAssessmentResultConfidence(LoadAssessmentResultValidationFieldsPort.Result validationFields, UUID param) {
        if (!Boolean.TRUE.equals(validationFields.isConfidenceValid())) {
            log.warn("The calculated confidence value is not valid for [assessmentId={}].", param);
            throw new ConfidenceCalculationNotValidException(CREATE_ADVICE_ASSESSMENT_RESULT_NOT_VALID);
        }
    }

    private void validateAssessmentResultCalculation(LoadAssessmentResultValidationFieldsPort.Result validationFields, UUID param) {
        if (!Boolean.TRUE.equals(validationFields.isCalculateValid())) {
            log.warn("The calculated result is not valid for [assessmentId={}].", param);
            throw new CalculateNotValidException(CREATE_ADVICE_ASSESSMENT_RESULT_NOT_VALID);
        }
    }

    private Result mapToResult(Plan solution) {
        var questionIdsMap = solution.getQuestions().stream()
            .filter(Question::isRecommended)
            .collect(Collectors.toMap(Question::getId, Function.identity()));

        var questions = loadAdviceImpactfulQuestionsPort.loadQuestions(questionIdsMap.keySet().stream().toList());

        var questionListItems = questions.stream().map(q -> {
                var question = questionIdsMap.get(q.id());
                var answeredOption = question.getCurrentOptionIndex() != null ? q.options().get(question.getCurrentOptionIndex()) : null;
                var recommendedOption = q.options().get(question.getRecommendedOptionIndex());
                var benefit = question.calculateBenefit();

                return new AdviceListItem(
                    new AdviceQuestion(q.id(), q.title(), q.index()),
                    answeredOption,
                    recommendedOption,
                    benefit,
                    q.attributes(),
                    q.questionnaire());
            }).sorted(Comparator.comparingDouble(AdviceListItem::benefit))
            .collect(Collectors.toList());

        return new Result(questionListItems);
    }
}
