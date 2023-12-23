package org.flickit.assessment.kit.application.port.out.kituseraccess;

import org.flickit.assessment.kit.application.domain.KitUser;

import java.util.Optional;
import java.util.UUID;

public interface LoadKitUserAccessPort {

    Optional<KitUser> loadByKitIdAndUserEmail(Long kitId, UUID userId);
}
