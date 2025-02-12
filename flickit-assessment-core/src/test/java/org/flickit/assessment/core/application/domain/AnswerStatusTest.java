package org.flickit.assessment.core.application.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AnswerStatusTest {

    @Test
    void testAnswerStatus_OrderOfItemsShouldNotBeChanged() {
        assertEquals(0, AnswerStatus.APPROVED.ordinal());
        assertEquals(1, AnswerStatus.UNAPPROVED.ordinal());
    }

    @Test
    void testAnswerStatus_IdOfItemsShouldNotBeChanged() {
        assertEquals(0, AnswerStatus.APPROVED.getId());
        assertEquals(1, AnswerStatus.UNAPPROVED.getId());
    }

    @Test
    void testAnswerStatus_NameOfItemsShouldNotBeChanged() {
        assertEquals("APPROVED", AnswerStatus.APPROVED.name());
        assertEquals("UNAPPROVED", AnswerStatus.UNAPPROVED.name());
    }

    @Test
    void testAnswerStatus_TitleOfItemsShouldNotBeChanged() {
        assertEquals("Approved", AnswerStatus.APPROVED.getTitle());
        assertEquals("Unapproved", AnswerStatus.UNAPPROVED.getTitle());
    }

    @Test
    void testAnswerStatus_SizeOfItemsShouldNotBeChanged() {
        assertEquals(2, AnswerStatus.values().length);
    }
}
