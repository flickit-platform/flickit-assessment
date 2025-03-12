package org.flickit.assessment.users.application.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpaceStatusTest {

    @Test
    void testSpaceStatus_OrderOfItemsShouldNotBeChanged() {
        assertEquals(0, SpaceStatus.ACTIVE.ordinal());
        assertEquals(1, SpaceStatus.INACTIVE.ordinal());
    }

    @Test
    void testSpaceStatus_NameOfItemsShouldNotBeChanged() {
        assertEquals("ACTIVE", SpaceStatus.ACTIVE.name());
        assertEquals("INACTIVE", SpaceStatus.INACTIVE.name());
    }

    @Test
    void testSpaceStatus_SizeOfItemsShouldNotBeChanged() {
        assertEquals(2, SpaceStatus.values().length);
    }
}
