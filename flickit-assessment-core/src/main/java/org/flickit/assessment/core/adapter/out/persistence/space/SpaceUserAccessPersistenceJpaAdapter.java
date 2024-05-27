package org.flickit.assessment.core.adapter.out.persistence.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.out.space.CheckSpaceAccessPort;
import org.flickit.assessment.core.application.port.out.space.LoadSpaceIdsByUserIdPort;
import org.flickit.assessment.data.jpa.users.spaceuseraccess.SpaceUserAccessJpaEntity;
import org.flickit.assessment.data.jpa.users.spaceuseraccess.SpaceUserAccessJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component("coreSpaceUserAccessPersistenceJpaAdapter")
@RequiredArgsConstructor
public class SpaceUserAccessPersistenceJpaAdapter implements LoadSpaceIdsByUserIdPort, CheckSpaceAccessPort {

    private final SpaceUserAccessJpaRepository repository;

    @Override
    public List<Long> loadSpaceIdsByUserId(UUID userId) {
        return repository.findByUserId(userId).stream()
            .map(SpaceUserAccessJpaEntity::getSpaceId)
            .toList();
    }

    @Override
    public boolean checkIsMember(long spaceId, UUID userId) {
        return repository.existsBySpaceIdAndUserId(spaceId, userId);
    }
}
