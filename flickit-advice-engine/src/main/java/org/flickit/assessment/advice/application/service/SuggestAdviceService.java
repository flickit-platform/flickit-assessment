package org.flickit.assessment.advice.application.service;

import ai.timefold.solver.core.api.solver.SolverFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.advice.application.domain.Plan;
import org.flickit.assessment.advice.application.domain.Question;
import org.flickit.assessment.advice.application.domain.advice.QuestionListItem;
import org.flickit.assessment.advice.application.port.in.SuggestAdviceUseCase;
import org.flickit.assessment.advice.application.port.out.LoadAdviceCalculationInfoPort;
import org.flickit.assessment.advice.application.port.out.question.LoadQuestionsPort;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SuggestAdviceService implements SuggestAdviceUseCase {

    private final LoadAdviceCalculationInfoPort loadInfoPort;
    private final SolverFactory<Plan> solverFactory;
    private final LoadQuestionsPort loadQuestionsPort;

    @Override
    public Result suggestAdvice(Param param) {
        // Load the problem
        var problem = loadInfoPort.load(param.getAssessmentId(), param.getTargets());

        // Solve the problem
        var solver = solverFactory.buildSolver();
        Plan solution = solver.solve(problem);

        return mapToResult(solution);
    }

    private Result mapToResult(Plan solution) {
        var questionMap = solution.getQuestions().stream()
            .filter(Question::isRecommended)
            .collect(Collectors.toMap(Question::getId, Function.identity()));

        var questions = loadQuestionsPort.loadQuestions(questionMap.keySet().stream().toList());

        var QuestionListItems = questions.stream().map(
                q ->
                {
                    Question question = questionMap.get(q.id());
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

        return new Result(QuestionListItems);
    }
}
