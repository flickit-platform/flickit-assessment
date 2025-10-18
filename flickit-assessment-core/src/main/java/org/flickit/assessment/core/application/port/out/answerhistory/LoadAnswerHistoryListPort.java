package org.flickit.assessment.core.application.port.out.answerhistory;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.core.application.domain.Answer;
import org.flickit.assessment.core.application.domain.FullUser;

import java.time.LocalDateTime;
import java.util.UUID;

public interface LoadAnswerHistoryListPort {

    PaginatedResponse<Result> load(UUID assessmentId, long questionId, int page, int size);

    record Result(Answer answer,
                  FullUser createdBy,
                  LocalDateTime creationTime,
                  Long answerOptionId,
                  Integer answerOptionIndex) {
    }
}
