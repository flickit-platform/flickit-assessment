package org.flickit.assessment.core.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class AnswerHistory {

    private final UUID id;
    private final Answer answer;
    private final UUID assessmentResultId;
    private final FullUser createdBy;
    private final LocalDateTime creationTime;
    private final HistoryType historyType;
}
