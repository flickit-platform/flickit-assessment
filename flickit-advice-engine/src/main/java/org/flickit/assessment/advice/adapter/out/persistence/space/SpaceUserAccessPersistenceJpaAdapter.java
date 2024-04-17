package org.flickit.assessment.advice.adapter.out.persistence.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.out.space.CheckSpaceAccessPort;
import org.flickit.assessment.data.jpa.users.spaceuseraccess.SpaceUserAccessJpaRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("adviceSpacePersistenceJpaAdapter")
@RequiredArgsConstructor
public class SpaceUserAccessPersistenceJpaAdapter implements
        CheckSpaceAccessPort {

    private final SpaceUserAccessJpaRepository repository;

    @Override
    public boolean checkIsMember(long spaceId, UUID userId) {
        return repository.existsByUserIdAndSpaceId(userId, spaceId);
    }
}
