package org.flickit.assessment.kit.application.port.out.questionnaire;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.Questionnaire;

import java.util.List;

public interface LoadQuestionnairesPort {

    /**
     * Loads questionnaires associated with a specific kit ID and kit's last version,
     * ordered by their index.
     *
     * @param kitId The ID of the kit for which questionnaires are to be loaded.
     * @return A list of questionnaires associated with the given kit ID, ordered by index.
     * @throws ResourceNotFoundException if the kit ID is not found.
     */
    List<Questionnaire> loadByKitId(Long kitId);
}
