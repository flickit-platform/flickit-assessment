package org.flickit.assessment.core.application.port.out.insight.subject;

import org.flickit.assessment.common.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

public interface ApproveSubjectInsightPort {

    void approve(UUID assessmentId, long subjectId, LocalDateTime lastModificationTime);

    /**
     * Approves all the unapproved insights associated with the subjects of assessment specified with assessment ID,
     * and updates the last modification time of those insights.
     *
     * @param assessmentId         The unique identifier (UUID) of the assessment.
     * @param lastModificationTime The {@link LocalDateTime} to set as the last modification time for the approved insights.
     * @throws ResourceNotFoundException If no assessment result is found for the given assessment ID.
     */
    void approveAll(UUID assessmentId, LocalDateTime lastModificationTime);

    /**
     * Approves all the insights associated with the subjects of assessment specified with assessment ID and subjectIds,
     * and updates the last modification time of those insights.
     *
     * @param assessmentId         The unique identifier (UUID) of the assessment.
     * @param subjectIds           A collection of unique identifiers (Long) of the subjects.
     * @param lastModificationTime The {@link LocalDateTime} to set as the last modification time for the approved insights.
     * @throws ResourceNotFoundException If no assessment result is found for the given assessment ID.
     */
    void approveAll(UUID assessmentId, Collection<Long> subjectIds, LocalDateTime lastModificationTime);
}
