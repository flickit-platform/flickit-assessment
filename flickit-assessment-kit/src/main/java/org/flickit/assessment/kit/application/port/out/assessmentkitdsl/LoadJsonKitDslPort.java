package org.flickit.assessment.kit.application.port.out.assessmentkitdsl;

import org.flickit.assessment.kit.application.domain.AssessmentKitDsl;

import java.util.Optional;

public interface LoadJsonKitDslPort {

    Optional<AssessmentKitDsl> load(Long id);
}
