package org.flickit.assessment.kit.application.port.out.kituser;

import org.flickit.assessment.kit.application.domain.KitUser;

import java.util.Optional;

public interface LoadKitUserByKitAndUserPort {

    Optional<KitUser> loadByKitAndUser(Long kitId, Long userId);
}
