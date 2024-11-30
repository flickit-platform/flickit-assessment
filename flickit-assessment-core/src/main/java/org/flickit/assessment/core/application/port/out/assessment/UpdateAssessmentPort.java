package org.flickit.assessment.core.application.port.out.assessment;

import org.flickit.assessment.common.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateAssessmentPort {

    Result update(AllParam param);

    record AllParam(UUID id,
                    String title,
                    String shortTitle,
                    String code,
                    LocalDateTime lastModificationTime,
                    UUID lastModifiedBy) {}

    record Result(UUID id) {}

    void updateLastModificationTime(UUID id, LocalDateTime lastModificationTime);

    /**
     * Updates the kitCustomId of an assessment.
     *
     * @param id          the UUID of the assessment for which the kitCustomId is going to be updated
     * @param kitCustomId the new kitCustomId to be associated to assessment
     * @throws ResourceNotFoundException if the related kit of the given kitCustom is not equal to the kit that the assessment is created on
     */
    void updateKitCustomId(UUID id, long kitCustomId);
}
