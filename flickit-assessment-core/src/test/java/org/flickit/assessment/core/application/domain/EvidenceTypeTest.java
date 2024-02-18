package org.flickit.assessment.core.application.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EvidenceTypeTest {

    @Test
    void testEvidenceType_IdOfTypesShouldNotBeChanged() {
        assertEquals(1, EvidenceType.POSITIVE.getId());
        assertEquals(2, EvidenceType.NEGATIVE.getId());

        assertEquals(2, EvidenceType.values().length);
    }
}
