package org.flickit.assessment.core.application.domain;

import org.flickit.assessment.core.test.fixture.application.AttributeMother;
import org.flickit.assessment.core.test.fixture.application.QuestionMother;
import org.junit.jupiter.api.Test;

import static org.flickit.assessment.core.test.fixture.application.MaturityLevelMother.levelTwo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class QuestionTest {

    @Test
    void testFindImpactByMaturityLevel() {
        Attribute attribute = AttributeMother.simpleAttribute();
        Question question = QuestionMother.withImpactsOnLevel24(attribute.getId());

        QuestionImpact impact = question.findImpactByAttributeAndMaturityLevel(attribute.getId(), levelTwo().getId());

        assertNotNull(impact);
        assertEquals(levelTwo().getId(), impact.getMaturityLevelId());
    }
}
