package org.flickit.assessment.advice.application.service.advice;

import ai.timefold.solver.core.api.solver.SolverManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.advice.application.domain.AttributeLevelTarget;
import org.flickit.assessment.advice.application.domain.Plan;
import org.flickit.assessment.advice.application.domain.Question;
import org.flickit.assessment.advice.application.domain.advice.QuestionRecommendation;
import org.flickit.assessment.advice.application.exception.FinalSolutionNotFoundException;
import org.flickit.assessment.advice.application.port.out.calculation.LoadAdviceCalculationInfoPort;
import org.flickit.assessment.advice.application.port.out.calculation.LoadCreatedAdviceDetailsPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.flickit.assessment.advice.common.ErrorMessageKey.CREATE_ADVICE_FINDING_BEST_SOLUTION_EXCEPTION;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CreateAdviceHelper {

    private final LoadAdviceCalculationInfoPort loadAdviceCalculationInfoPort;
    private final SolverManager<Plan, UUID> solverManager;
    private final LoadCreatedAdviceDetailsPort loadCreatedAdviceDetailsPort;

    public List<QuestionRecommendation> createAdvice(UUID assessmentId, List<AttributeLevelTarget> attributeLevelTargets) {
        var problem = loadAdviceCalculationInfoPort.loadAdviceCalculationInfo(assessmentId, attributeLevelTargets);
        var solution = solverManager.solve(UUID.randomUUID(), problem);
        Plan plan;
        try {
            plan = solution.getFinalBestSolution();
        } catch (InterruptedException e) {
            log.error("Finding best solution for assessment {} interrupted", assessmentId, e.getCause());
            Thread.currentThread().interrupt();
            throw new FinalSolutionNotFoundException(CREATE_ADVICE_FINDING_BEST_SOLUTION_EXCEPTION);
        } catch (ExecutionException e) {
            log.error("Error occurred while calculating best solution for assessment {}", assessmentId, e.getCause());
            throw new FinalSolutionNotFoundException(CREATE_ADVICE_FINDING_BEST_SOLUTION_EXCEPTION);
        }
        return mapToResult(plan, assessmentId);
    }

    private List<QuestionRecommendation> mapToResult(Plan solution, UUID assessmentId) {
        var questionIdsMap = solution.getQuestions().stream()
            .filter(Question::isRecommended)
            .collect(Collectors.toMap(Question::getId, Function.identity()));

        var adviceQuestionDetails = loadCreatedAdviceDetailsPort.loadAdviceDetails(questionIdsMap.keySet().stream().toList(), assessmentId);

        return adviceQuestionDetails.stream().map(adv -> {
                var question = questionIdsMap.get(adv.question().id());
                var answeredOption = question.getCurrentOptionIndex() != null ? adv.options().get(question.getCurrentOptionIndex()) : null;
                var recommendedOption = adv.options().get(question.getRecommendedOptionIndex());
                var benefit = question.calculateBenefit();

                return new QuestionRecommendation(
                    adv.question(),
                    answeredOption,
                    recommendedOption,
                    benefit,
                    adv.attributes(),
                    adv.questionnaire());
            }).sorted(Comparator.comparingDouble(QuestionRecommendation::benefit).reversed())
            .toList();
    }
}
