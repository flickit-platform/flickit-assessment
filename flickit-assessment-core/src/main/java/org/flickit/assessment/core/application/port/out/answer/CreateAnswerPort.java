package org.flickit.assessment.core.application.port.out.answer;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AnswerStatus;

import java.util.UUID;

public interface CreateAnswerPort {

    /**
     * @throws ResourceNotFoundException if no assessmentResult found by the given id
     */
    UUID persist(Param param);

    record Param(UUID assessmentResultId,
                 Long questionnaireId,
                 Long questionId,
                 Long answerOptionId,
                 Integer confidenceLevelId,
                 Boolean isNotApplicable,
                 AnswerStatus status,
                 UUID currentUserId) {
    }
}
