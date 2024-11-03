package org.flickit.assessment.advice.application.service.advice;

import ai.timefold.solver.test.api.score.stream.ConstraintVerifier;
import org.flickit.assessment.advice.application.domain.AttributeLevelScore;
import org.flickit.assessment.advice.application.domain.Plan;
import org.flickit.assessment.advice.application.domain.Question;
import org.flickit.assessment.advice.test.fixture.application.QuestionMother;
import org.junit.jupiter.api.Test;

import static org.flickit.assessment.advice.application.service.advice.PlanConstraintProvider.SOFT_SCORE_FACTOR;

class PlanConstraintProviderMultipleTargetTest {

    ConstraintVerifier<PlanConstraintProvider, Plan> constraintVerifier = ConstraintVerifier.build(
        new PlanConstraintProvider(), Plan.class, Question.class);

    @Test
    void gainLeastTest_PenalizesWhenQuestionsGainIsLessThanTarget() {
        AttributeLevelScore attributeLevelScore = new AttributeLevelScore(0, 10, 0, 1);
        Question question1 = QuestionMother.createQuestionWithTargetAndOptionIndexes(attributeLevelScore, 0, 1);
        Question question2 = QuestionMother.createQuestionWithTargetAndOptionIndexes(attributeLevelScore, 0, 0);

        AttributeLevelScore attributeLevelScore2 = new AttributeLevelScore(0, 10, 0, 2);
        Question question3 = QuestionMother.createQuestionWithTargetAndOptionIndexes(attributeLevelScore2, 0, 3);
        Question question4 = QuestionMother.createQuestionWithTargetAndOptionIndexes(attributeLevelScore2, 0, 1);

        constraintVerifier.verifyThat(PlanConstraintProvider::minGain)
            .given(question1, question2, attributeLevelScore, question3, question4, attributeLevelScore2)
            .penalizesBy(8);
    }

    @Test
    void gainLeastTest_PenalizesWhenNoQuestionChosen() {
        AttributeLevelScore attributeLevelScore = new AttributeLevelScore(0, 12, 0, 1);
        Question question1 = QuestionMother.createQuestionWithTargetAndOptionIndexes(attributeLevelScore, 0, 0);
        Question question2 = QuestionMother.createQuestionWithTargetAndOptionIndexes(attributeLevelScore, 0, 0);

        AttributeLevelScore attributeLevelScore2 = new AttributeLevelScore(0, 10, 0, 2);
        Question question3 = QuestionMother.createQuestionWithTargetAndOptionIndexes(attributeLevelScore2, 0, 0);
        Question question4 = QuestionMother.createQuestionWithTargetAndOptionIndexes(attributeLevelScore2, 0, 0);

        constraintVerifier.verifyThat(PlanConstraintProvider::minGain)
            .given(
                attributeLevelScore,
                question1,
                question2,
                attributeLevelScore2,
                question3,
                question4
            )
            .penalizesBy(22);
    }

    @Test
    void totalBenefit() {
        AttributeLevelScore attributeLevelScore = new AttributeLevelScore(2, 12, 0, 1);
        Question question = QuestionMother.createQuestionWithTargetAndOptionIndexes(attributeLevelScore, 0, 3);

        AttributeLevelScore attributeLevelScore2 = new AttributeLevelScore(0, 10, 0, 2);
        Question question2 = QuestionMother.createQuestionWithTargetAndOptionIndexes(attributeLevelScore2, 0, 3);

        constraintVerifier.verifyThat(PlanConstraintProvider::totalBenefit)
            .given(question, attributeLevelScore, question2, attributeLevelScore2)
            .rewardsWith(80 * SOFT_SCORE_FACTOR);
    }

    @Test
    void leastCount() {
        AttributeLevelScore attributeLevelScore = new AttributeLevelScore(0, 10, 0, 1);
        Question question1 = QuestionMother.createQuestionWithTargetAndOptionIndexes(attributeLevelScore, 0, 1);
        Question question2 = QuestionMother.createQuestionWithTargetAndOptionIndexes(attributeLevelScore, 0, 2);
        Question question3 = QuestionMother.createQuestionWithTargetAndOptionIndexes(attributeLevelScore, 1, 1);

        AttributeLevelScore attributeLevelScore2 = new AttributeLevelScore(0, 10, 0, 2);
        Question question4 = QuestionMother.createQuestionWithTargetAndOptionIndexes(attributeLevelScore2, 0, 0);
        Question question5 = QuestionMother.createQuestionWithTargetAndOptionIndexes(attributeLevelScore2, 0, 2);
        Question question6 = QuestionMother.createQuestionWithTargetAndOptionIndexes(attributeLevelScore2, 0, 0);

        constraintVerifier.verifyThat(PlanConstraintProvider::leastCount)
            .given(question1, question2, question3, attributeLevelScore,
                question4, question5, question6, attributeLevelScore2)
            .penalizesBy(3 * SOFT_SCORE_FACTOR);
    }
}
