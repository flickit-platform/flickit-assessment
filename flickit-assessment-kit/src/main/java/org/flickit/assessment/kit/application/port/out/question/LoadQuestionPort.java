package org.flickit.assessment.kit.application.port.out.question;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.Question;

public interface LoadQuestionPort {

    /**
     * Loads question associated with a specific kit ID,
     * @param id The ID of needed question
     * @param kitId The ID of the kit for which question is belonged to it.
     * @return A Question  associated with the given ID and kit ID.
     * @throws ResourceNotFoundException if the question or kit with given IDs are not found.
     * Or the question does not belong to that kit
     */
    Question load(long id, long kitId);
}
