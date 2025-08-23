package org.flickit.assessment.core.application.port.out.insight.attribute;

import org.flickit.assessment.common.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

public interface ApproveAttributeInsightPort {

    void approve(UUID assessmentId, long attributeId, LocalDateTime lastModificationTime);

    /**
     * Approves all the unapproved insights associated with the attributes of assessment specified with assessment ID,
     * and updates the last modification time of those insights.
     *
     * @param assessmentId         The unique identifier (UUID) of the assessment.
     * @param lastModificationTime The {@link LocalDateTime} to set as the last modification time for the approved insights.
     * @throws ResourceNotFoundException If no assessment result is found for the given assessment ID.
     */
    void approveAll(UUID assessmentId, LocalDateTime lastModificationTime);

    /**
     * Approves all insights associated with the specified attribute IDs for a given assessment,
     * and updates the last modification time of those insights.
     *
     * @param assessmentId         The unique identifier (UUID) of the assessment.
     * @param attributeIds         A collection of unique identifiers (Long) of the attributes.
     * @param lastModificationTime The {@link LocalDateTime} to set as the last modification time for the approved insights.
     * @throws ResourceNotFoundException If no assessment result is found for the given assessment ID.
     */
    void approveAll(UUID assessmentId, Collection<Long> attributeIds, LocalDateTime lastModificationTime);
}
