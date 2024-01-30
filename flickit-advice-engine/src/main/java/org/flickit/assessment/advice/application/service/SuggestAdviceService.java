package org.flickit.assessment.advice.application.service;

import ai.timefold.solver.core.api.solver.Solver;
import ai.timefold.solver.core.api.solver.SolverFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.advice.application.domain.Plan;
import org.flickit.assessment.advice.application.domain.Question;
import org.flickit.assessment.advice.application.port.in.SuggestAdviceUseCase;
import org.flickit.assessment.advice.application.port.out.LoadAdviceCalculationInfoPort;
import org.flickit.assessment.advice.application.port.out.question.LoadAdviceQuestionsPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SuggestAdviceService implements SuggestAdviceUseCase {

    private final LoadAdviceCalculationInfoPort loadInfoPort;
    private final SolverFactory<Plan> solverFactory;
    private final LoadAdviceQuestionsPort loadQuestionsPort;

    @Override
    public Result suggestAdvice(Param param) {
        // Load the problem
        Plan problem = loadInfoPort.loadAdviceCalculationInfo(param.getAssessmentId(), param.getTargets());

        // Solve the problem
        Solver<Plan> solver = solverFactory.buildSolver();
        Plan solution = solver.solve(problem);



        return mapToResult(solution);
    }

    private Result mapToResult(Plan solution) {
        List<Long> questionIds = solution.getQuestions().stream()
            .filter(Question::isRecommended)
            .map(Question::getId).toList();

        loadQuestionsPort.load(questionIds);

        return null;
    }
}
