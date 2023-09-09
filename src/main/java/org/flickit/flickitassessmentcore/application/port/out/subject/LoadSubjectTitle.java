package org.flickit.flickitassessmentcore.application.port.out.subject;

import org.flickit.flickitassessmentcore.application.service.exception.CalculateNotValidException;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;

public interface LoadSubjectTitle {

    /**
     * Loads subject title by the given {@code subjectId}.
     *
     * @param subjectId The ID of the subject to load the title for.
     * @return The loaded subject title String.
     * @throws ResourceNotFoundException  If the subject value is not found.
     * @throws CalculateNotValidException If the subject value is not valid.
     */
    String load(Long subjectId);
}
