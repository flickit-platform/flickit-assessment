package org.flickit.assessment.users.application.port.out.expertgroupaccess;

import org.flickit.assessment.users.application.domain.ExpertGroupAccess;

import java.util.Optional;
import java.util.UUID;

public interface LoadExpertGroupAccessPort {

    Optional<ExpertGroupAccess> loadExpertGroupAccess(long expertGroupId, UUID userId);
}
