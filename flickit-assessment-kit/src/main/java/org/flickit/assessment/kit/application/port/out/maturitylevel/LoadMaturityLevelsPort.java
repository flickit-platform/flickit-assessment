package org.flickit.assessment.kit.application.port.out.maturitylevel;


import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.MaturityLevel;

import java.util.List;

public interface LoadMaturityLevelsPort {

    /**
     * Loads maturity levels associated with a specific kit ID and kit's last version,
     * ordered by their index.
     *
     * @param kitVersionId The kitVersionId of the kit for which maturity levels are to be loaded.
     * @return A list of maturity level associated with the given kit ID, ordered by index.
     * @throws ResourceNotFoundException if the kit ID is not found.
     */
    List<MaturityLevel> loadByKitVersionId(Long kitVersionId);
}
