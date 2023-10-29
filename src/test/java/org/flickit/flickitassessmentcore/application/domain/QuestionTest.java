package org.flickit.flickitassessmentcore.application.domain;

import org.flickit.flickitassessmentcore.test.fixture.application.MaturityLevelMother;
import org.flickit.flickitassessmentcore.test.fixture.application.QuestionMother;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class QuestionTest {

    @Test
    void testFindImpactByMaturityLevel() {
        Question question = QuestionMother.withImpactsOnLevel24();

        QuestionImpact impact = question.findImpactByMaturityLevel(MaturityLevelMother.levelTwo());

        assertNotNull(impact);
        assertEquals(MaturityLevelMother.levelTwo().getId(), impact.getMaturityLevelId());
    }
}
