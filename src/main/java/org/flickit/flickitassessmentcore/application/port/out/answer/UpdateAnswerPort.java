package org.flickit.flickitassessmentcore.application.port.out.answer;

import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;

import java.util.UUID;

public interface UpdateAnswerPort {

    /**
     * @throws ResourceNotFoundException if no assessmentResult found by the given id
     */
    UUID update(Param param);

    record Param(UUID id,
                 UUID assessmentResultId,
                 Long questionId,
                 Long answerOptionId) {
    }
}
