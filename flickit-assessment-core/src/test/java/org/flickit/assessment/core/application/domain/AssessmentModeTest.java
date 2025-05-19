package org.flickit.assessment.core.application.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AssessmentModeTest {

    @Test
    void testAssessmentMode_OrderOfItemsShouldNotBeChanged() {
        assertEquals(0, AssessmentMode.QUICK.ordinal());
        assertEquals(1, AssessmentMode.ADVANCED.ordinal());
    }

    @Test
    void testAssessmentMode_IdOfItemsShouldNotBeChanged() {
        assertEquals(0, AssessmentMode.QUICK.getId());
        assertEquals(1, AssessmentMode.ADVANCED.getId());
    }

    @Test
    void testAssessmentMode_NameOfItemsShouldNotBeChanged() {
        assertEquals("QUICK", AssessmentMode.QUICK.name());
        assertEquals("ADVANCED", AssessmentMode.ADVANCED.name());
    }

    @Test
    void testAssessmentMode_ValueOfByIdMethodShouldReturnCorrectMode() {
        assertEquals(AssessmentMode.QUICK, AssessmentMode.valueOfById(0));
        assertEquals(AssessmentMode.ADVANCED, AssessmentMode.valueOfById(1));
    }

    @Test
    void testAssessmentMode_SizeOfItemsShouldNotBeChanged() {
        assertEquals(2, AssessmentMode.values().length);
    }
}
