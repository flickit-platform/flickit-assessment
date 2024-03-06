package org.flickit.assessment.kit.application.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KitVersionStatusTest {

    @Test
    void testKitVersionStatus_OrderOfItemsShouldNotBeChanged() {
        assertEquals(0, KitVersionStatus.ACTIVE.ordinal());
        assertEquals(1, KitVersionStatus.UPDATING.ordinal());
        assertEquals(2, KitVersionStatus.ARCHIVE.ordinal());
    }

    @Test
    void testKitVersionStatus_NameOfItemsShouldNotBeChanged() {
        assertEquals("ACTIVE", KitVersionStatus.ACTIVE.name());
        assertEquals("UPDATING", KitVersionStatus.UPDATING.name());
        assertEquals("ARCHIVE", KitVersionStatus.ARCHIVE.name());
    }

    @Test
    void testKitVersionStatus_SizeOfItemsShouldNotBeChanged() {
        assertEquals(3, KitVersionStatus.values().length);
    }
}
