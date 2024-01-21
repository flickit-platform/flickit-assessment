package org.flickit.assessment.advice.application.service;

import ai.timefold.solver.test.api.score.stream.ConstraintVerifier;
import org.flickit.assessment.advice.application.domain.Plan;
import org.flickit.assessment.advice.application.domain.Question;
import org.flickit.assessment.advice.application.domain.Target;
import org.junit.jupiter.api.Test;

import java.util.List;

import static application.service.QuestionMother.createQuestionWithTargetAndOptionIndexes;

class PlanConstraintProviderTest {
    ConstraintVerifier<PlanConstraintProvider, Plan> constraintVerifier = ConstraintVerifier.build(
        new PlanConstraintProvider(), Plan.class, Question.class);


    @Test
    void gainLeastTest_PenalizesWhenQuestionsGainIsLessThanTarget() {
        Target target = new Target(0, 10);

        Question question1 = createQuestionWithTargetAndOptionIndexes(target, 0, 1);
        Question question2 = createQuestionWithTargetAndOptionIndexes(target, 0, 0);

        target.setQuestions(List.of(question1, question2));

        constraintVerifier.verifyThat(PlanConstraintProvider::minGain)
            .given(question1, question2, target)
            .penalizesBy(8);
    }

    @Test
    void gainLeastTest_PenalizesWhenNoQuestionChosen() {
        Target target = new Target(0, 12);

        Question question1 = createQuestionWithTargetAndOptionIndexes(target, 0, 0);
        Question question2 = createQuestionWithTargetAndOptionIndexes(target, 0, 0);

        target.setQuestions(List.of(question1, question2));

        constraintVerifier.verifyThat(PlanConstraintProvider::minGain)
            .given(
                target,
                question1,
                question2
            )
            .penalizesBy(12);
    }

    @Test
    void totalBenefit() {
        Target target = new Target(2, 12);

        Question question = createQuestionWithTargetAndOptionIndexes(target, 0, 3);

        constraintVerifier.verifyThat(PlanConstraintProvider::totalBenefit)
            .given(question)
            .rewardsWith(80);
    }

    @Test
    void leastCount() {
        Target target = new Target(0, 10);

        Question question1 = createQuestionWithTargetAndOptionIndexes(target, 0, 1);
        Question question2 = createQuestionWithTargetAndOptionIndexes(target, 0, 2);
        Question question3 = createQuestionWithTargetAndOptionIndexes(target, 1, 1);

        constraintVerifier.verifyThat(PlanConstraintProvider::leastCount)
            .given(question1, question2, question3, target)
            .penalizesBy(2);
    }
}
