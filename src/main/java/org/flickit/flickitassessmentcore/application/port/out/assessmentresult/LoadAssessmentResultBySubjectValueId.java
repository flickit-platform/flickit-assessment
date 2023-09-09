package org.flickit.flickitassessmentcore.application.port.out.assessmentresult;

import org.flickit.flickitassessmentcore.application.domain.AssessmentResult;
import org.flickit.flickitassessmentcore.application.service.exception.CalculateNotValidException;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;

import java.util.UUID;

public interface LoadAssessmentResultBySubjectValueId {

    /**
     * Loads a result by the given {@code subValueId}.
     *
     * @param subValueId The ID of the subject value to load the result for.
     * @return The loaded AssessmentResult object.
     * @throws ResourceNotFoundException  If the subject value is not found.
     * @throws CalculateNotValidException If the subject value is not valid.
     */
    AssessmentResult load(UUID subValueId);
}
