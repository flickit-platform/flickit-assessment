package org.flickit.assessment.users.adapter.out.persistence.spaceuseraccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.users.spaceuseraccess.SpaceUserAccessJpaEntity;
import org.flickit.assessment.data.jpa.users.spaceuseraccess.SpaceUserAccessJpaRepository;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SpaceUserMemberAccessJpaAdapter implements
    AddSpaceMemberPort,
    CheckSpaceMemberAccessPort {

    private final SpaceUserAccessJpaRepository repository;

    @Override
    public void persist(Param param) {
        SpaceUserAccessJpaEntity unsavedEntity = new SpaceUserAccessJpaEntity(param.spaceId(), param.invitee(),
            param.inviter(), param.creationTime());
        repository.save(unsavedEntity);
    }

    @Override
    public boolean checkIsMember(long spaceId, UUID userId) {
        return repository.existsByUserIdAndSpaceId(userId, spaceId);
    }
}
