package org.flickit.flickitassessmentcore.application.port.out;

import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;

import java.util.UUID;

public interface SoftDeleteAssessmentPort {

    /**
     * @throws ResourceNotFoundException if no assessment found by the given id
     */
    void softDeleteAndSetDeletionTimeById(Param param);

    record Param(UUID id, Long deletionTime){
    }
}
