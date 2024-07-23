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

    @Test
    void testEvidenceType_NameOfItemsShouldNotBeChanged() {
        assertEquals("PERSIST",HistoryType.PERSIST.name());
        assertEquals("UPDATE", HistoryType.UPDATE.name());
        assertEquals("DELETE", HistoryType.DELETE.name());
    }

    @Test
    void testEvidenceType_SizeOfItemsShouldNotBeChanged() {
        assertEquals(3, HistoryType.values().length);
    }
}
