package org.flickit.flickitassessmentcore.application.port.out.answer;

import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;

import java.util.UUID;

public interface CreateAnswerPort {

    /**
     * @throws ResourceNotFoundException if no assessmentResult found by the given id
     */
    UUID persist(Param param);

    record Param(UUID assessmentResultId,
                 Long questionId,
                 Long answerOptionId) {
    }
}
