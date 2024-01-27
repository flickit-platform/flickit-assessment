package org.flickit.assessment.advice.application.service;

import ai.timefold.solver.core.api.solver.Solver;
import ai.timefold.solver.core.api.solver.SolverFactory;
import ai.timefold.solver.core.config.solver.SolverConfig;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.advice.application.domain.AttributeLevelScore;
import org.flickit.assessment.advice.application.domain.Option;
import org.flickit.assessment.advice.application.domain.Plan;
import org.flickit.assessment.advice.application.domain.Question;
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
        AttributeLevelScore attributeLevelScore = new AttributeLevelScore(10, 50, 0, 1);
        AttributeLevelScore attributeLevelScore2 = new AttributeLevelScore(10, 32, 0, 2);

        List<Question> questions = new ArrayList<>();
        questions.add(createQuestionWithTargetAndOptionIndexes(attributeLevelScore, 0));
        questions.add(createQuestionWithTargetAndOptionIndexes(attributeLevelScore, 1));
        questions.add(createQuestionWithTargetAndOptionIndexes(attributeLevelScore, 3));
        questions.add(createQuestionWithTargetAndOptionIndexes(attributeLevelScore, 0));
        questions.add(createQuestionWithTargetAndOptionIndexes(attributeLevelScore, 0));
        questions.add(createQuestionWithTargetAndOptionIndexes(attributeLevelScore, 0));
        questions.add(createQuestionWithTargetAndOptionIndexes(attributeLevelScore, 0));
        questions.add(createQuestionWithTargetAndOptionIndexes(attributeLevelScore2, 0));
        questions.add(createQuestionWithTargetAndOptionIndexes(attributeLevelScore2, 0));
        questions.add(createQuestionWithTargetAndOptionIndexes(attributeLevelScore2, 0));
        questions.add(createQuestionWithTargetAndOptionIndexes(attributeLevelScore2, 0));
        questions.add(createQuestionWithTargetAndOptionIndexes(attributeLevelScore2, 0));
        questions.add(createQuestionWithTargetAndOptionIndexes(attributeLevelScore, attributeLevelScore2, 1));
        questions.add(createQuestionWithTargetAndOptionIndexes(attributeLevelScore, attributeLevelScore2, 0));

        return new Plan(List.of(attributeLevelScore, attributeLevelScore2), questions);
    }

    public static Question createQuestionWithTargetAndOptionIndexes(AttributeLevelScore attributeLevelScore, int currentOptionIndex) {
        return new Question(id++, 10, createOptions(attributeLevelScore), currentOptionIndex);
    }

    public static List<Option> createOptions(AttributeLevelScore attributeLevelScore) {
        int index = 1;
        List<Option> options = new ArrayList<>();
        options.add(createOption(index++, attributeLevelScore, 0, 0, 10));
        options.add(createOption(index++, attributeLevelScore, 2, 0.25, 10));
        options.add(createOption(index++, attributeLevelScore, 4, 0.5, 10));
        options.add(createOption(index, attributeLevelScore, 8, 1.0, 10));
        return options;
    }

    public static Option createOption(int index, AttributeLevelScore attributeLevelScore, double gainValue, double progress, int questionCost) {
        HashMap<AttributeLevelScore, Double> gains = new HashMap<>();
        gains.put(attributeLevelScore, gainValue);
        return new Option(id++, index, gains, progress, questionCost);
    }

    public static Question createQuestionWithTargetAndOptionIndexes(AttributeLevelScore attributeLevelScore1, AttributeLevelScore attributeLevelScore2, int currentOptionIndex) {
        return new Question(id++, 10, createOptions(attributeLevelScore1, attributeLevelScore2), currentOptionIndex);
    }

    public static List<Option> createOptions(AttributeLevelScore attributeLevelScore1, AttributeLevelScore attributeLevelScore2) {
        int index = 1;
        List<Option> options = new ArrayList<>();
        options.add(createOption(index++, attributeLevelScore1, attributeLevelScore2, 0, 0, 10));
        options.add(createOption(index++, attributeLevelScore1, attributeLevelScore2, 2, 0.25, 10));
        options.add(createOption(index++, attributeLevelScore1, attributeLevelScore2, 4, 0.5, 10));
        options.add(createOption(index, attributeLevelScore1, attributeLevelScore2, 8, 1.0, 10));
        return options;
    }

    public static Option createOption(int index, AttributeLevelScore attributeLevelScore1, AttributeLevelScore attributeLevelScore2, double gainValue, double progress, int questionCost) {
        HashMap<AttributeLevelScore, Double> gains = new HashMap<>();
        gains.put(attributeLevelScore1, gainValue);
        gains.put(attributeLevelScore2, gainValue);
        return new Option(id++, index, gains, progress, questionCost);
    }
}
