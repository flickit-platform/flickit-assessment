package org.flickit.flickitassessmentcore.application.port.out.subjectvalue;

import org.flickit.flickitassessmentcore.application.domain.SubjectValue;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;

import java.util.Optional;
import java.util.UUID;

public interface LoadSubjectValuePort {

    /**
     * Loads a subject value by the given {@code subjectId}.
     *
     * @param subjectId The ID of the subject to load the value for.
     * @param resultId  The ID of the result to load the value for.
     * @return The loaded SubjectValue object.
     * @throws ResourceNotFoundException If the subject value is not found.
     */
    Optional<SubjectValue> load(Long subjectId, UUID resultId);
}
