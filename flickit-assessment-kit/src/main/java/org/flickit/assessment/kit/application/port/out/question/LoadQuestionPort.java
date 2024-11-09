package org.flickit.assessment.kit.application.port.out.question;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.Question;

import java.util.List;
import java.util.Set;

public interface LoadQuestionPort {

    /**
     * Loads question associated with a specific kit ID,
     * @param id The ID of needed question
     * @param kitVersionId The ID of the kit version for which question is belonged to it.
     * @return A Question  associated with the given ID and kit version ID.
     * @throws ResourceNotFoundException if the question or kit version with given IDs are not found.
     * Or the question does not belong to that kit version
     */
    Question load(long id, long kitVersionId);

    List<Question> loadAllByIdInAndKitVersion(Set<Long> ids, long kitVersionId);
}
