package org.flickit.assessment.kit.application.port.out.assessmentkit;

import org.flickit.assessment.kit.application.domain.User;

public interface LoadAssessmentKitOwnerPort {
    User loadKitOwnerById(Long kitId);
}
