package org.flickit.assessment.advice.application.port.out.assessment;

import org.flickit.assessment.common.application.domain.ID;

import java.util.UUID;

public interface LoadAssessmentKitVersionIdPort {

    Long loadKitVersionIdById(ID assessmentId);
}
