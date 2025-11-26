package org.flickit.assessment.core.application.port.out.answerhistory;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.core.application.domain.AnswerStatus;
import org.flickit.assessment.core.application.domain.FullUser;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public interface LoadAnswerHistoryPort {

    PaginatedResponse<Result> load(UUID assessmentId, long questionId, int page, int size);

    record Result(Long answerOptionId,
                  Integer answerOptionIndex,
                  Integer confidenceLevelId,
                  Boolean isNotApplicable,
                  AnswerStatus answerStatus,
                  FullUser createdBy,
                  LocalDateTime creationTime) {
    }

    Map<Long, Integer> countAnswerHistories(UUID assessmentResultId, Long questionnaireId);

    int countQuestionAnswerHistories(UUID assessmentResultId, Long questionId);
}
