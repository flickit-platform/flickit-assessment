package org.flickit.assessment.advice.application.service.advice;

import ai.timefold.solver.core.api.solver.SolverManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.advice.application.domain.Plan;
import org.flickit.assessment.advice.application.domain.Question;
import org.flickit.assessment.advice.application.domain.advice.QuestionListItem;
import org.flickit.assessment.advice.application.exception.CanNotFindFinalSolutionException;
import org.flickit.assessment.advice.application.port.in.SuggestAdviceUseCase;
import org.flickit.assessment.advice.application.port.out.LoadAdviceCalculationInfoPort;
import org.flickit.assessment.advice.application.port.out.assessment.CheckUserAssessmentAccessPort;
import org.flickit.assessment.advice.application.port.out.assessment.LoadAssessmentResultValidationFieldsPort;
import org.flickit.assessment.advice.application.port.out.question.LoadQuestionsPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.CalculateNotValidException;
import org.flickit.assessment.common.exception.ConfidenceCalculationNotValidException;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.flickit.assessment.advice.common.ErrorMessageKey.SUGGEST_ADVICE_ASSESSMENT_RESULT_NOT_VALID;
import static org.flickit.assessment.advice.common.ErrorMessageKey.SUGGEST_ADVICE_FINDING_BEST_SOLUTION_EXCEPTION;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Slf4j
@RequiredArgsConstructor
public class SuggestAdviceService implements SuggestAdviceUseCase {

    private final CheckUserAssessmentAccessPort checkUserAssessmentAccessPort;
    private final LoadAssessmentResultValidationFieldsPort loadAssessmentResultValidationFieldsPort;
    private final LoadAdviceCalculationInfoPort loadInfoPort;
    private final SolverManager<Plan, UUID> solverManager;
    private final LoadQuestionsPort loadQuestionsPort;

    @Override
    public Result suggestAdvice(Param param) {
        checkUserAccess(param.getAssessmentId(), param.getCurrentUserId());
        checkAssessmentResultValidity(param.getAssessmentId());

        var problem = loadInfoPort.loadAdviceCalculationInfo(param.getAssessmentId(), param.getTargets());
        var solution = solverManager.solve(UUID.randomUUID(), problem);
        Plan plan;
        try {
            plan = solution.getFinalBestSolution();
        } catch (InterruptedException e) {
            log.error("Finding best solution for assessment {} interrupted", param.getAssessmentId(), e.getCause());
            throw new CanNotFindFinalSolutionException(SUGGEST_ADVICE_FINDING_BEST_SOLUTION_EXCEPTION);
        } catch (ExecutionException e) {
            log.error("Error occurred while calculating best solution for assessment {}", param.getAssessmentId(), e.getCause());
            throw new CanNotFindFinalSolutionException(SUGGEST_ADVICE_FINDING_BEST_SOLUTION_EXCEPTION);
        }
        return mapToResult(plan);
    }

    private void checkUserAccess(UUID assessmentId, UUID currentUserId) {
        if (!checkUserAssessmentAccessPort.hasAccess(assessmentId, currentUserId))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }

    private void checkAssessmentResultValidity(UUID assessmentId) {
        var validationFields = loadAssessmentResultValidationFieldsPort.loadValidationFields(assessmentId);

        if (!Boolean.TRUE.equals(validationFields.isCalculateValid())) {
            log.warn("The calculated result is not valid for [assessmentId={}].", assessmentId);
            throw new CalculateNotValidException(SUGGEST_ADVICE_ASSESSMENT_RESULT_NOT_VALID);
        }

        if (!Boolean.TRUE.equals(validationFields.isConfidenceValid())) {
            log.warn("The calculated confidence value is not valid for [assessmentId={}].", assessmentId);
            throw new ConfidenceCalculationNotValidException(SUGGEST_ADVICE_ASSESSMENT_RESULT_NOT_VALID);
        }
    }

    private Result mapToResult(Plan solution) {
        var questionIdsMap = solution.getQuestions().stream()
            .filter(Question::isRecommended)
            .collect(Collectors.toMap(Question::getId, Function.identity()));

        var questions = loadQuestionsPort.loadQuestions(questionIdsMap.keySet().stream().toList());

        var questionListItems = questions.stream().map(q -> {
                var question = questionIdsMap.get(q.id());
                var currentOptionIndex = question.getCurrentOptionIndex() != null ? question.getCurrentOptionIndex() + 1 : null;
                var recommendedOptionIndex = question.getRecommendedOptionIndex() + 1;
                var benefit = question.calculateBenefit();

                return new QuestionListItem(
                    q.id(),
                    q.title(),
                    q.index(),
                    currentOptionIndex,
                    recommendedOptionIndex,
                    benefit,
                    q.options(),
                    q.attributes(),
                    q.questionnaire());
            }).sorted(Comparator.comparingDouble(QuestionListItem::benefit))
            .collect(Collectors.toList());

        return new Result(questionListItems);
    }
}
