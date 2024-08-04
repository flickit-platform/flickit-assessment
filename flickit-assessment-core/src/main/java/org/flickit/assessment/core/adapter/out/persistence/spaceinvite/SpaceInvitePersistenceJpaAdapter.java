package org.flickit.assessment.core.adapter.out.persistence.spaceinvite;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.out.space.CreateSpaceInvitePort;
import org.flickit.assessment.data.jpa.users.spaceinvitee.SpaceInviteeJpaEntity;
import org.flickit.assessment.data.jpa.users.spaceinvitee.SpaceInviteeJpaRepository;
import org.springframework.stereotype.Component;

import static org.flickit.assessment.core.adapter.out.persistence.spaceinvite.SpaceInviteMapper.mapToJpaEntity;

@Component("coreSpaceInvitePersistenceJpaAdapter")
@RequiredArgsConstructor
public class SpaceInvitePersistenceJpaAdapter implements CreateSpaceInvitePort {

    private final SpaceInviteeJpaRepository repository;

    @Override
    public void persist(CreateSpaceInvitePort.Param param) {
        var invitation = repository.findBySpaceIdAndEmail(param.spaceId(), param.email());

        SpaceInviteeJpaEntity entity;
        entity = invitation.map(e -> mapToJpaEntity(invitation.get().getId(), param))
            .orElseGet(() -> mapToJpaEntity(null, param));

        repository.save(entity);
    }
}
