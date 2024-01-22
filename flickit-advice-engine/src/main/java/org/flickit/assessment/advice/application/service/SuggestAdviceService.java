package org.flickit.assessment.advice.application.service;

import ai.timefold.solver.core.api.solver.Solver;
import ai.timefold.solver.core.api.solver.SolverFactory;
import ai.timefold.solver.core.config.solver.SolverConfig;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.advice.application.domain.Option;
import org.flickit.assessment.advice.application.domain.Plan;
import org.flickit.assessment.advice.application.domain.Question;
import org.flickit.assessment.advice.application.domain.Target;
import org.flickit.assessment.advice.application.port.in.SuggestAdviceUseCase;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
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
            .filter(Question::isRecommended)
            .toList();
        questions.forEach(question -> log.info(question.toString()));
    }

    private static long id = 0L;

    public static Plan generateDemoData() {
        Target target = new Target(10, 50);
        Target target2 = new Target(10, 32);
//
//        long id = 0L;
        List<Question> questions = new ArrayList<>();
        questions.add(createQuestionWithTargetAndOptionIndexes(target, 0));
        questions.add(createQuestionWithTargetAndOptionIndexes(target, 1));
        questions.add(createQuestionWithTargetAndOptionIndexes(target, 3));
        questions.add(createQuestionWithTargetAndOptionIndexes(target, 0));
        questions.add(createQuestionWithTargetAndOptionIndexes(target, 0));
        questions.add(createQuestionWithTargetAndOptionIndexes(target, 0));
        questions.add(createQuestionWithTargetAndOptionIndexes(target, 0));
        questions.add(createQuestionWithTargetAndOptionIndexes(target2, 0));
        questions.add(createQuestionWithTargetAndOptionIndexes(target2, 0));
        questions.add(createQuestionWithTargetAndOptionIndexes(target2, 0));
        questions.add(createQuestionWithTargetAndOptionIndexes(target2, 0));
        questions.add(createQuestionWithTargetAndOptionIndexes(target2, 0));
        questions.add(createQuestionWithTargetAndOptionIndexes(target, target2, 1));
        questions.add(createQuestionWithTargetAndOptionIndexes(target, target2, 0));

        return new Plan(List.of(target, target2), questions);
//        return null;
    }

    public static Question createQuestionWithTargetAndOptionIndexes(Target target, int currentOptionIndex) {
        return new Question(id++, 10, createOptions(target), currentOptionIndex);
    }

    public static List<Option> createOptions(Target target) {
        List<Option> options = new ArrayList<>();
        options.add(createOption(target, 0, 0, 10));
        options.add(createOption(target, 2, 0.25, 10));
        options.add(createOption(target, 4, 0.5, 10));
        options.add(createOption(target, 8, 1.0, 10));
        return options;
    }

    public static Option createOption(Target target, double gainValue, double progress, int questionCost) {
        HashMap<Target, Double> gains = new HashMap<>();
        gains.put(target, gainValue);
        return new Option(gains, progress, questionCost);
    }

    public static Question createQuestionWithTargetAndOptionIndexes(Target target1, Target target2, int currentOptionIndex) {
        return new Question(id++, 10, createOptions(target1, target2), currentOptionIndex);
    }

    public static List<Option> createOptions(Target target1, Target target2) {
        List<Option> options = new ArrayList<>();
        options.add(createOption(target1, target2, 0, 0, 10));
        options.add(createOption(target1, target2, 2, 0.25, 10));
        options.add(createOption(target1, target2, 4, 0.5, 10));
        options.add(createOption(target1, target2, 8, 1.0, 10));
        return options;
    }

    public static Option createOption(Target target1, Target target2, double gainValue, double progress, int questionCost) {
        HashMap<Target, Double> gains = new HashMap<>();
        gains.put(target1, gainValue);
        gains.put(target2, gainValue);
        return new Option(gains, progress, questionCost);
    }
}
