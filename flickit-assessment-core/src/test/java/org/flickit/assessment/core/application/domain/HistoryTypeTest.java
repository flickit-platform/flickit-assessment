package org.flickit.assessment.core.application.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HistoryTypeTest {

    @Test
    void testHistoryType_OrderOfItemsShouldNotBeChanged() {
        assertEquals(0, HistoryType.PERSIST.ordinal());
        assertEquals(1, HistoryType.UPDATE.ordinal());
        assertEquals(2, HistoryType.DELETE.ordinal());
    }
}
