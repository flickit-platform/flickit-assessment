package org.flickit.assessment.core.application.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AssessmentColorTest {

    @Test
    void testAssessmentColor_IdOfColorsShouldNotBeChanged() {
        assertEquals(1, AssessmentColor.CRAYOLA.getId());
        assertEquals(2, AssessmentColor.CORAL.getId());
        assertEquals(3, AssessmentColor.SUN_GLOW.getId());
        assertEquals(4, AssessmentColor.EMERALD.getId());
        assertEquals(5, AssessmentColor.BLUE.getId());
        assertEquals(6, AssessmentColor.MIDNIGHT_GREEN.getId());

        assertEquals(6, AssessmentColor.values().length);
    }
}
