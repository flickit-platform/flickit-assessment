package org.flickit.assessment.kit.application.port.out.assessmentkit;

import java.util.Optional;

public interface LoadExpertGroupIdPort {

    Optional<Long> loadExpertGroupId(Long kitId);
}
