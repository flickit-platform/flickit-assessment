package org.flickit.assessment.common.application.domain.adviceitem;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PriorityLevelTest {

    @Test
    void testPriorityLevel_OrderOfItemsShouldNotBeChanged() {
        assertEquals(0, PriorityLevel.LOW.ordinal());
        assertEquals(1, PriorityLevel.MEDIUM.ordinal());
        assertEquals(2, PriorityLevel.HIGH.ordinal());
    }

    @Test
    void testPriorityLevel_NameOfItemsShouldNotBeChanged() {
        assertEquals("LOW", PriorityLevel.LOW.name());
        assertEquals("MEDIUM", PriorityLevel.MEDIUM.name());
        assertEquals("HIGH", PriorityLevel.HIGH.name());
    }

    @Test
    void testPriorityLevel_SizeOfItemsShouldNotBeChanged() {
        assertEquals(3, PriorityLevel.values().length);
    }

    /**
     *  If the className has changed, messages with the PriorityLevel prefix
     *  (in the messages.properties file) should also be updated
     */
    @Test
    void testPriorityLevel_ClassNameShouldNotBeChanged() {
        assertEquals("PriorityLevel", PriorityLevel.class.getSimpleName());
    }
}
