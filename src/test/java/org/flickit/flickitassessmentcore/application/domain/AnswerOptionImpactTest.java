package org.flickit.flickitassessmentcore.application.domain;

import org.flickit.flickitassessmentcore.application.domain.mother.AnswerOptionImpactMother;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AnswerOptionImpactTest {

    @Test
    void testCalculateScore() {
        AnswerOptionImpact impact = AnswerOptionImpactMother.onLevelTwo(0.1);

        double score = impact.calculateScore();
        assertEquals(0.1 * impact.getQuestionImpact().getWeight(), score);
    }
}
