package org.flickit.flickitassessmentcore.domain.calculate;

import org.flickit.flickitassessmentcore.domain.calculate.mother.AnswerOptionImpactMother;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AnswerOptionImpactTest {

    @Test
    void calculateScore() {
        AnswerOptionImpact impact = AnswerOptionImpactMother.onLevelTwo(0.1);

        double score = impact.calculateScore();
        assertEquals(0.1 * impact.getQuestionImpact().getWeight(), score);
    }
}
