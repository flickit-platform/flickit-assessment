package org.flickit.assessment.advice.application.domain.adviceitem;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CostLevelTest {

    @Test
    void testCostLevel_OrderOfItemsShouldNotBeChanged() {
        assertEquals(0, CostLevel.LOW.ordinal());
        assertEquals(1, CostLevel.MEDIUM.ordinal());
        assertEquals(2, CostLevel.HIGH.ordinal());
    }

    @Test
    void testCostLevel_NameOfItemsShouldNotBeChanged() {
        assertEquals("LOW", CostLevel.LOW.name());
        assertEquals("MEDIUM", CostLevel.MEDIUM.name());
        assertEquals("HIGH", CostLevel.HIGH.name());
    }

    @Test
    void testCostLevel_SizeOfItemsShouldNotBeChanged() {
        assertEquals(3, CostLevel.values().length);
    }
}
