package org.flickit.assessment.advice.application.domain.adviceitem;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    /**
     *  If the className has changed, messages with the CostLevel prefix
     *  (in the messages.properties file) should also be updated
     */
    @Test
    void testCostLevel_ClassNameShouldNotBeChanged() {
        assertEquals("CostLevel", CostLevel.class.getSimpleName());
    }
}
