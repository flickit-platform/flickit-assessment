package org.flickit.assessment.kit.application.port.out.maturitylevel;


import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.MaturityLevel;

public interface LoadMaturityLevelsPort {

    /**
     * Loads maturity levels associated with a specific kit ID and kit's last version,
     * ordered by their index.
     *
     * @param kitVersionId The kitVersionId of the kit for which maturity levels are to be loaded.
     * @param size The size of page
     * @param page The number of page
     * @return A list of maturity level associated with the given kit ID, ordered by index.
     * @throws ResourceNotFoundException if the kit ID is not found.
     */
    PaginatedResponse<MaturityLevel> loadByKitVersionId(long kitVersionId, Integer size, Integer page);
}
