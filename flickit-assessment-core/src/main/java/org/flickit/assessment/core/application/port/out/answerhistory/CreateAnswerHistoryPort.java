package org.flickit.assessment.core.application.port.out.answerhistory;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AnswerHistory;
import org.flickit.assessment.core.application.domain.HistoryType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CreateAnswerHistoryPort {

    /**
     * @throws ResourceNotFoundException if no assessmentResult found by the given id
     */
    UUID persist(AnswerHistory answerHistory);

    void persistAll(List<AnswerHistory> answerHistories, UUID assessmentResultId);

    void persistOnClearAnswers(PersistOnClearAnswersParam param);

    record PersistOnClearAnswersParam(UUID assessmentResultId,
                                      List<Long> questionIds,
                                      UUID createdBy,
                                      LocalDateTime creationTime,
                                      HistoryType type) {

    }
}
