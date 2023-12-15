package org.flickit.assessment.kit.application.port.out.kituser;

import org.flickit.assessment.kit.application.domain.KitUser;

import java.util.Optional;
import java.util.UUID;

public interface LoadKitUserByKitAndUserPort {

    Optional<KitUser> loadByKitAndUser(Long kitId, UUID userId);
}
