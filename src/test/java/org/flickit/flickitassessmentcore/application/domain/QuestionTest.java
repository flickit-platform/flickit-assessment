package org.flickit.flickitassessmentcore.application.domain;

import org.flickit.flickitassessmentcore.application.domain.mother.MaturityLevelMother;
import org.flickit.flickitassessmentcore.application.domain.mother.QuestionMother;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class QuestionTest {

    @Test
    void findImpactByMaturityLevel() {
        Question question = QuestionMother.withImpactsOnLevel24();

        QuestionImpact impact = question.findImpactByMaturityLevel(MaturityLevelMother.levelTwo());

        assertNotNull(impact);
        assertEquals(MaturityLevelMother.levelTwo().getId(), impact.getMaturityLevelId());
    }
}
