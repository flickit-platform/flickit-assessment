package org.flickit.assessment.core.application.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AnalysisTypeTest {

    @Test
    void testAnalysisType_OrderOfItemsShouldNotBeChanged() {
        assertEquals(0, AnalysisType.CODE_QUALITY.ordinal());
    }

    @Test
    void testAnalysisType_IdOfItemsShouldNotBeChanged() {
        assertEquals(1, AnalysisType.CODE_QUALITY.getId());
    }

    @Test
    void testAnalysisType_NameOfItemsShouldNotBeChanged() {
        assertEquals("CODE_QUALITY",AnalysisType.CODE_QUALITY.name());
    }

    @Test
    void testAnalysisType_SizeOfItemsShouldNotBeChanged() {
        assertEquals(1, AnalysisType.values().length);
    }
}