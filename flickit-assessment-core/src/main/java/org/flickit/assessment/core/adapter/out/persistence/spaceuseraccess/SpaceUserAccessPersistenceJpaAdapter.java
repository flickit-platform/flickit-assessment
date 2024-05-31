package org.flickit.assessment.core.adapter.out.persistence.spaceuseraccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.data.jpa.users.spaceuseraccess.SpaceUserAccessJpaRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("coreSpaceUserAccessPersistenceJpaAdapter")
@RequiredArgsConstructor
public class SpaceUserAccessPersistenceJpaAdapter implements CheckSpaceAccessPort {

    private final SpaceUserAccessJpaRepository repository;

    @Override
    public boolean checkIsMember(long spaceId, UUID userId) {
        return repository.existsBySpaceIdAndUserId(spaceId, userId);
    }
}
