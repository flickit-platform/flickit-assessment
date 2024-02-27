package org.flickit.assessment.core.application.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EvidenceTypeTest {

    @Test
    void testEvidenceType_OrderOfItemsShouldNotBeChanged() {
        assertEquals(0, EvidenceType.POSITIVE.ordinal());
        assertEquals(1, EvidenceType.NEGATIVE.ordinal());
    }

    @Test
    void testEvidenceType_NameOfItemsShouldNotBeChanged() {
        assertEquals("POSITIVE", EvidenceType.POSITIVE.name());
        assertEquals("NEGATIVE", EvidenceType.NEGATIVE.name());
    }

    @Test
    void testEvidenceType_TitleOfItemsShouldNotBeChanged() {
        assertEquals("Positive", EvidenceType.POSITIVE.getTitle());
        assertEquals("Negative", EvidenceType.NEGATIVE.getTitle());
    }

    @Test
    void testEvidenceType_SizeOfItemsShouldNotBeChanged() {
        assertEquals(2, EvidenceType.values().length);
    }
}
