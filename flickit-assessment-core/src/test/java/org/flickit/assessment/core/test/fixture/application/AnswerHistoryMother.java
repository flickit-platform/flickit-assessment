package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.Answer;
import org.flickit.assessment.core.application.domain.AnswerHistory;
import org.flickit.assessment.core.application.domain.FullUser;
import org.flickit.assessment.core.application.domain.HistoryType;

import java.time.LocalDateTime;
import java.util.UUID;

public class AnswerHistoryMother {

    public static AnswerHistory history(Answer answer) {
        return new AnswerHistory(UUID.randomUUID(), answer, UUID.randomUUID(),
            new FullUser(UUID.randomUUID(), "displayName", "email@gmail.com", "path/path"),
            LocalDateTime.now(), HistoryType.PERSIST);
    }
}
