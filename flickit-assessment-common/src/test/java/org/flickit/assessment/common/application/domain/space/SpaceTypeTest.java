package org.flickit.assessment.common.application.domain.space;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpaceTypeTest {

    @Test
    void testSpaceType_OrderOfItemsShouldNotBeChanged() {
        assertEquals(0, SpaceType.BASIC.ordinal());
        assertEquals(1, SpaceType.PREMIUM.ordinal());
    }

    @Test
    void testSpaceType_NameOfItemsShouldNotBeChanged() {
        assertEquals("BASIC", SpaceType.BASIC.name());
        assertEquals("PREMIUM", SpaceType.PREMIUM.name());
    }

    @Test
    void testSpaceType_SizeOfItemsShouldNotBeChanged() {
        assertEquals(2, SpaceType.values().length);
    }
}
