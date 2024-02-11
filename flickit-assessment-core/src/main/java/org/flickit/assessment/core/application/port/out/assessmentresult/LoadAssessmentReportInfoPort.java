package org.flickit.assessment.core.application.port.out.assessmentresult;

import org.flickit.assessment.common.exception.CalculateNotValidException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentResult;

import java.util.UUID;

public interface LoadAssessmentReportInfoPort {

    /**
     * Loads the assessment report info by the given {@code assessmentId}.
     *
     * @param assessmentId The ID of the assessment to load the result for.
     * @return The loaded AssessmentResult object.
     * @throws ResourceNotFoundException  If the assessment result is not found.
     * @throws CalculateNotValidException If the assessment result is not valid.
     */
    AssessmentResult load(UUID assessmentId);
}
