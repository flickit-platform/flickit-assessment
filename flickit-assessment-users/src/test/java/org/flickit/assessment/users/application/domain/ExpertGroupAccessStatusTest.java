package org.flickit.assessment.users.application.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExpertGroupAccessStatusTest {

    @Test
    void testExpertGroupAccessStatus_OrderOfItemsShouldNotBeChanged() {
        assertEquals(0, ExpertGroupAccessStatus.PENDING.ordinal());
        assertEquals(1, ExpertGroupAccessStatus.ACTIVE.ordinal());
    }

    @Test
    void testExpertGroupAccessStatus_NameOfItemsShouldNotBeChanged() {
        assertEquals("PENDING", ExpertGroupAccessStatus.PENDING.name());
        assertEquals("ACTIVE", ExpertGroupAccessStatus.ACTIVE.name());
    }

    @Test
    void testExpertGroupAccessStatus_SizeOfItemsShouldNotBeChanged() {
        assertEquals(2, ExpertGroupAccessStatus.values().length);
    }
}
