package org.flickit.assessment.advice.application.service.advice;

import ai.timefold.solver.test.api.score.stream.ConstraintVerifier;
import org.flickit.assessment.advice.application.domain.AttributeLevelScore;
import org.flickit.assessment.advice.application.domain.Plan;
import org.flickit.assessment.advice.application.domain.Question;
import org.junit.jupiter.api.Test;

import static org.flickit.assessment.advice.application.service.advice.PlanConstraintProvider.SOFT_SCORE_FACTOR;
import static org.flickit.assessment.advice.test.fixture.application.QuestionMother.createQuestionWithTargetAndOptionIndexes;

class PlanConstraintProviderTest {
    ConstraintVerifier<PlanConstraintProvider, Plan> constraintVerifier = ConstraintVerifier.build(
        new PlanConstraintProvider(), Plan.class, Question.class);


    @Test
    void gainLeastTest_PenalizesWhenQuestionsGainIsLessThanTarget() {
        AttributeLevelScore attributeLevelScore = new AttributeLevelScore(0, 10, 0, 1);
        Question question1 = createQuestionWithTargetAndOptionIndexes(attributeLevelScore, null, 1);
        Question question2 = createQuestionWithTargetAndOptionIndexes(attributeLevelScore, 0, 0);

        constraintVerifier.verifyThat(PlanConstraintProvider::minGain)
            .given(question1, question2, attributeLevelScore)
            .penalizesBy(8);
    }

    @Test
    void gainLeastTest_PenalizesWhenNoQuestionChosen() {
        AttributeLevelScore attributeLevelScore = new AttributeLevelScore(0, 12, 0, 1);
        Question question1 = createQuestionWithTargetAndOptionIndexes(attributeLevelScore, 0, 0);
        Question question2 = createQuestionWithTargetAndOptionIndexes(attributeLevelScore, 0, 0);

        constraintVerifier.verifyThat(PlanConstraintProvider::minGain)
            .given(
                attributeLevelScore,
                question1,
                question2
            )
            .penalizesBy(12);
    }

    @Test
    void totalBenefit() {
        AttributeLevelScore attributeLevelScore = new AttributeLevelScore(2, 12, 0, 1);
        Question question = createQuestionWithTargetAndOptionIndexes(attributeLevelScore, 0, 3);

        constraintVerifier.verifyThat(PlanConstraintProvider::totalBenefit)
            .given(question)
            .rewardsWith(80 * SOFT_SCORE_FACTOR);
    }

    @Test
    void leastCount() {
        AttributeLevelScore attributeLevelScore = new AttributeLevelScore(0, 10, 0, 1);
        Question question1 = createQuestionWithTargetAndOptionIndexes(attributeLevelScore, 0, 1);
        Question question2 = createQuestionWithTargetAndOptionIndexes(attributeLevelScore, 0, 2);
        Question question3 = createQuestionWithTargetAndOptionIndexes(attributeLevelScore, 1, 1);

        constraintVerifier.verifyThat(PlanConstraintProvider::leastCount)
            .given(question1, question2, question3, attributeLevelScore)
            .penalizesBy(2 * SOFT_SCORE_FACTOR);
    }
}
