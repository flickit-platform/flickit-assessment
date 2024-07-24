package org.flickit.assessment.core.application.port.out.answerhistory;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AnswerHistory;

import java.util.UUID;

public interface CreateAnswerHistoryPort {

    /**
     * @throws ResourceNotFoundException if no assessmentResult found by the given id
     */
    UUID persist(AnswerHistory answerHistory);
}
