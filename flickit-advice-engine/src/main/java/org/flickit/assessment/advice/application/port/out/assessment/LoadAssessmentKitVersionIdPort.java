package org.flickit.assessment.advice.application.port.out.assessment;

import java.util.UUID;

public interface LoadAssessmentKitVersionIdPort {

    Long loadKitVersionIdById(UUID assessmentId);
}
