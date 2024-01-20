package application.service;

import ai.timefold.solver.core.api.solver.Solver;
import ai.timefold.solver.core.api.solver.SolverFactory;
import ai.timefold.solver.core.config.solver.SolverConfig;
import application.domain.Plan;
import application.domain.Question;
import application.port.in.SuggestAdviceUseCase;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.List;

@Slf4j
public class SuggestAdviceService implements SuggestAdviceUseCase {
    @Override
    public void suggestAdvice() {
        SolverFactory<Plan> solverFactory = SolverFactory
            .create(new SolverConfig()
                .withSolutionClass(Plan.class)
                .withEntityClasses(Question.class)
                .withConstraintProviderClass(PlanConstraintProvider.class)
                // The solver runs only for 5 seconds on this small dataset.
                // It's recommended to run for at least 5 minutes ("5m") otherwise.
                .withTerminationSpentLimit(Duration.ofSeconds(2)));

        // Load the problem
        Plan problem = generateDemoData();

        // Solve the problem
        Solver<Plan> solver = solverFactory.buildSolver();
        Plan solution = solver.solve(problem);

        // Visualize the solution
        printPlan(solution);
    }

    private static void printPlan(Plan solution) {
        log.info("score is: " + solution.getScore());

        List<Question> questions = solution.getQuestions().stream()
            .filter(Question::hasGain)
            .toList();
        questions.forEach(question -> log.info(question.toString()));
    }

    public static Plan generateDemoData() {
        return null;
    }
}
