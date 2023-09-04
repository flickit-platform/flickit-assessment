package org.flickit.flickitassessmentcore.application.port.out.assessmentresult;

import org.flickit.flickitassessmentcore.application.domain.AssessmentResult;
import org.flickit.flickitassessmentcore.application.exception.CalculateNotValidException;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;

import java.util.UUID;

public interface LoadAssessmentReportInfoPort {

    /**
     * Loads an {@link AssessmentResult} based on the provided assessment ID.
     *
     * @param assessmentId The UUID of the assessment to load the result for.
     * @return The loaded AssessmentResult object.
     * @throws ResourceNotFoundException  If the assessment result is not found.
     * @throws CalculateNotValidException If the assessment result is not valid.
     */
    AssessmentResult load(UUID assessmentId);
}
