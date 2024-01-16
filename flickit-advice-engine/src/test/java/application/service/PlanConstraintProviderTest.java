package application.service;

import ai.timefold.solver.test.api.score.stream.ConstraintVerifier;
import application.domain.Plan;
import application.domain.Question;
import application.domain.Target;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlanConstraintProviderTest {
    ConstraintVerifier<PlanConstraintProvider, Plan> constraintVerifier = ConstraintVerifier.build(
        new PlanConstraintProvider(), Plan.class, Question.class);

    @Test
    void gainLeastTest_PenalizesWhenQuestionsGainIsLessThanTarget() {
        Target target = new Target(10);

        Question question = new Question(0L, target, 8, 10, 0.0, List.of((double) 0, 0.25, 0.5, 1.0));
        question.setOption(0.25);
        Question question2 = new Question(1L, target, 8, 10, 0.0, List.of((double) 0, 0.25, 0.5, 1.0));
        question2.setOption(0.0);

        constraintVerifier.verifyThat(PlanConstraintProvider::minGain)
            .given(question, question2, target)
            .penalizesBy(8);
    }

    @Test
    void gainLeastTest_PenalizesWhenNoQuestionChosen() {
        Target target = new Target(12);

        long id = 0L;

        var question1 = new Question(id++, target, 10, 4, 0.0, List.of((double) 0, 0.25, 0.5, 1.0));
        var question2 = new Question(id++, target, 10, 4, 0.0, List.of((double) 0, 0.25, 0.5, 1.0));
        var question3 = new Question(id++, target, 10, 11, 0.0, List.of((double) 0, 0.25, 0.5, 1.0));
        question1.setOption(0.0);
        question2.setOption(0.0);
        question3.setOption(0.0);

        constraintVerifier.verifyThat(PlanConstraintProvider::minGain)
            .given(
                target,
                question1,
                question2,
                question3
            )
            .penalizesBy(12);
    }

    @Test
    void totalBenefit() {
        Target target = new Target(10);

        Question question = new Question(0L, target, 8, 10, 0.0, List.of((double) 0, 0.25, 0.5, 1.0));
        question.setOption(0.5);

        new Question(1L, target, 10, 4, 0.0, List.of((double) 0, 0.25, 0.5, 1.0));

        constraintVerifier.verifyThat(PlanConstraintProvider::totalBenefit)
            .given(question)
            .rewardsWith(80);
    }

    @Test
    void leastCount() {
        Target target = new Target(10);

        Question question1 = new Question(0L, target, 8, 10, 0.0, List.of((double) 0, 0.25, 0.5, 1.0));
        question1.setOption(0.25);
        Question question2 = new Question(1L, target, 8, 10, 0.0, List.of((double) 0, 0.25, 0.5, 1.0));
        question2.setOption(0.5);
        Question question3 = new Question(2L, target, 8, 5, 0.25, List.of((double) 0.25, 0.5, 1.0));
        question3.setOption(0.25);

        constraintVerifier.verifyThat(PlanConstraintProvider::leastCount)
            .given(question1, question2, question3, target)
            .penalizesBy(2);
    }
}
