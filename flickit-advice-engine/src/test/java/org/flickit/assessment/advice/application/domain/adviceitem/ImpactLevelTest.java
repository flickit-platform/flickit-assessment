package org.flickit.assessment.advice.application.domain.adviceitem;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ImpactLevelTest {

    @Test
    void testImpactLevel_OrderOfItemsShouldNotBeChanged() {
        assertEquals(0, ImpactLevel.LOW.ordinal());
        assertEquals(1, ImpactLevel.MEDIUM.ordinal());
        assertEquals(2, ImpactLevel.HIGH.ordinal());
    }

    @Test
    void testImpactLevel_NameOfItemsShouldNotBeChanged() {
        assertEquals("LOW",ImpactLevel.LOW.name());
        assertEquals("MEDIUM", ImpactLevel.MEDIUM.name());
        assertEquals("HIGH", ImpactLevel.HIGH.name());
    }

    @Test
    void testImpactLevel_SizeOfItemsShouldNotBeChanged() {
        assertEquals(3, ImpactLevel.values().length);
    }
}
