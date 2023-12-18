package org.flickit.assessment.kit.application.port.out.expertgroup;

import java.util.Optional;
import java.util.UUID;

public interface LoadExpertGroupOwnerPort {

    Optional<UUID> loadOwnerId(Long expertGroupId);
}
