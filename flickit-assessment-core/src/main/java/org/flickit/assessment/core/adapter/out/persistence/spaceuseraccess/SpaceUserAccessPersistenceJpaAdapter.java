package org.flickit.assessment.core.adapter.out.persistence.spaceuseraccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.domain.SpaceUserAccess;
import org.flickit.assessment.core.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.core.application.port.out.spaceuseraccess.SpaceUserAccessPort;
import org.flickit.assessment.data.jpa.users.spaceuseraccess.SpaceUserAccessJpaEntity;
import org.flickit.assessment.data.jpa.users.spaceuseraccess.SpaceUserAccessJpaRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("coreSpaceUserAccessPersistenceJpaAdapter")
@RequiredArgsConstructor
public class SpaceUserAccessPersistenceJpaAdapter implements
    CheckSpaceAccessPort,
    SpaceUserAccessPort {

    private final SpaceUserAccessJpaRepository repository;

    @Override
    public boolean checkIsMember(long spaceId, UUID userId) {
        return repository.existsBySpaceIdAndUserId(spaceId, userId);
    }

    @Override
    public void persist(SpaceUserAccess access) {
        SpaceUserAccessJpaEntity unsavedEntity = new SpaceUserAccessJpaEntity(access.getSpaceId(), access.getUserId(),
            access.getCreatedBy(), access.getCreationTime(), access.getCreationTime());
        repository.save(unsavedEntity);
    }
}
