package org.flickit.assessment.kit.application.port.out.kituseraccess;

import org.flickit.assessment.kit.application.domain.KitUser;

import java.util.Optional;

public interface LoadKitUserAccessPort {

    Optional<KitUser> loadByKitIdAndUserEmail(Long kitId, String email);
}
