package org.flickit.assessment.core.application.port.out.assessmentkit;

import org.flickit.assessment.core.application.domain.AssessmentKit;

import java.util.List;
import java.util.UUID;

public interface LoadAccessibleKitsByUserIdPort {

    List<AssessmentKit> loadAccessibleKitsByUserId(UUID userId);
}
