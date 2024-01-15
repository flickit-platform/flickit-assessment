package application.service;

import ai.timefold.solver.core.api.solver.Solver;
import ai.timefold.solver.core.api.solver.SolverFactory;
import ai.timefold.solver.core.config.solver.SolverConfig;
import application.domain.Plan;
import application.domain.Question;
import application.domain.Target;
import application.port.in.SuggestAdviceUseCase;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

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
        System.out.println("score is: " + solution.getScore());

        List<Question> questions = solution.getQuestions().stream()
            .filter(q -> q.getGainRatio() != 0)
            .toList();
        questions.forEach(System.out::println);

    }

    public static Plan generateDemoData() {
        Target target = new Target(13);

        long id = 0L;
        List<Question> questions = new ArrayList<>();
        questions.add(new Question(id++, target, 5, 40, 0.0, List.of((double) 0, 0.25, 0.5, 0.75, 1.0)));
        questions.add(new Question(id++, target, 6, 4, 0.0, List.of((double) 0, 0.25, 0.5, 0.75, 1.0)));
        questions.add(new Question(id++, target, 4, 4, 0.0, List.of((double) 0, 0.25, 0.5, 0.75, 1.0)));

        return new Plan(target, questions);
    }
}
