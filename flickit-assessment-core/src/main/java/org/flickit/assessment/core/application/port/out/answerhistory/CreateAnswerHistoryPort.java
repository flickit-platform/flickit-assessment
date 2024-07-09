package org.flickit.assessment.core.application.port.out.answerhistory;

import org.flickit.assessment.common.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CreateAnswerHistoryPort {

    /**
     * @throws ResourceNotFoundException if no assessmentResult found by the given id
     */
    UUID persist(Param param);

    record Param(UUID answerId,
                 UUID assessmentResultId,
                 Long questionId,
                 Long answerOptionId,
                 Integer confidenceLevelId,
                 Boolean isNotApplicable,
                 UUID createdBy,
                 LocalDateTime creationTime,
                 int historyTypeId) {
    }
}
