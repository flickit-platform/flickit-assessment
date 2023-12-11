package org.flickit.assessment.kit.application.port.out.assessmentkit;

import org.flickit.assessment.kit.application.domain.AssessmentKit;

import java.util.Optional;

public interface LoadKitByIdPort {

    Optional<AssessmentKit> load(Long kitId);
}
