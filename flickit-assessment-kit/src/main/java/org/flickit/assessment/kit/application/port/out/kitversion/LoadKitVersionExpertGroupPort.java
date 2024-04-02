package org.flickit.assessment.kit.application.port.out.kitversion;

import org.flickit.assessment.common.exception.ResourceNotFoundException;

public interface LoadKitVersionExpertGroupPort {

    /**
     * @throws ResourceNotFoundException if no kitVersion found by the given id
     */
    Long loadKitVersionExpertGroupId(Long kitVersionId);
}
