package org.flickit.flickitassessmentcore.application.port.out.subjectvalue;

import org.flickit.flickitassessmentcore.application.domain.SubjectValue;
import org.flickit.flickitassessmentcore.application.service.exception.CalculateNotValidException;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;

import java.util.UUID;

public interface LoadSubjectValueBySubjectIdAndAssessmentResultPort {

    /**
     * Loads a subject value by the given {@code subjectId}.
     *
     * @param subjectId The ID of the subject to load the value for.
     * @param resultId The ID of the result to load the value for.
     * @return The loaded SubjectValue object.
     * @throws ResourceNotFoundException  If the subject value is not found.
     * @throws CalculateNotValidException If the subject value is not valid.
     */
    SubjectValue load(Long subjectId, UUID resultId);
}
