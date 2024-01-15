package org.flickit.assessment.kit.application.port.out.expertgroup;

import java.util.Optional;

public interface LoadExpertGroupIdPort {

    Optional<Long> loadId(Long expertGroupId);
}
