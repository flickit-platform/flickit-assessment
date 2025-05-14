package org.flickit.assessment.core.application.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VisibilityTypeTest {

    @Test
    void testVisibilityType_OrderOfItemsShouldNotBeChanged() {
        assertEquals(0, VisibilityType.RESTRICTED.ordinal());
        assertEquals(1, VisibilityType.PUBLIC.ordinal());
    }

    @Test
    void testVisibilityType_NameOfItemsShouldNotBeChanged() {
        assertEquals("RESTRICTED", VisibilityType.RESTRICTED.name());
        assertEquals("PUBLIC", VisibilityType.PUBLIC.name());
    }

    @Test
    void testVisibilityType_SizeOfItemsShouldNotBeChanged() {
        assertEquals(2, VisibilityType.values().length);
    }
}
