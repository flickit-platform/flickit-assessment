package org.flickit.assessment.advice.application.port.out.assessment;

import java.util.UUID;

public interface LoadAssessmentResultValidationFieldsPort {

    Result loadValidationFields(UUID assessmentId);

    record Result(boolean isCalculateValid, boolean isConfidenceValid) {
    }
}
