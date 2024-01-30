package org.flickit.assessment.advice.application.service;

import ai.timefold.solver.core.api.solver.SolverFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.advice.application.domain.Plan;
import org.flickit.assessment.advice.application.domain.Question;
import org.flickit.assessment.advice.application.domain.advice.QuestionListItem;
import org.flickit.assessment.advice.application.port.in.SuggestAdviceUseCase;
import org.flickit.assessment.advice.application.port.out.LoadAdviceCalculationInfoPort;
import org.flickit.assessment.advice.application.port.out.assessment.CheckUserAssessmentAccessPort;
import org.flickit.assessment.advice.application.port.out.question.LoadQuestionsPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Slf4j
@RequiredArgsConstructor
public class SuggestAdviceService implements SuggestAdviceUseCase {

    private final CheckUserAssessmentAccessPort checkUserAssessmentAccessPort;
    private final LoadAdviceCalculationInfoPort loadInfoPort;
    private final SolverFactory<Plan> solverFactory;
    private final LoadQuestionsPort loadQuestionsPort;

    @Override
    public Result suggestAdvice(Param param) {
        checkUserAccess(param.getAssessmentId(), param.getCurrentUserId());

        var problem = loadInfoPort.load(param.getAssessmentId(), param.getTargets());
        var solution = solverFactory.buildSolver().solve(problem);

        return mapToResult(solution);
    }

    private void checkUserAccess(UUID assessmentId, UUID currentUserId) {
        if (!checkUserAssessmentAccessPort.hasAccess(assessmentId, currentUserId))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }

    private Result mapToResult(Plan solution) {
        var questionIdsMap = solution.getQuestions().stream()
            .filter(Question::isRecommended)
            .collect(Collectors.toMap(Question::getId, Function.identity()));

        var questions = loadQuestionsPort.loadQuestions(questionIdsMap.keySet().stream().toList());

        var questionListItems = questions.stream().map(
                q ->
                {
                    Question question = questionIdsMap.get(q.id());
                    return new QuestionListItem(
                        q.id(),
                        q.title(),
                        q.index(),
                        question.getCurrentOptionIndex() + 1,
                        question.getRecommendedOptionIndex() + 1,
                        question.calculateBenefit(),
                        q.options(),
                        q.attributes(),
                        q.questionnaire()
                    );
                })
            .sorted(Comparator.comparingDouble(QuestionListItem::benefit))
            .collect(Collectors.toList());

        return new Result(questionListItems);
    }
}
