package org.flickit.assessment.core.adapter.out.persistence.spaceinvitee;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.out.space.InviteSpaceMemberPort;
import org.flickit.assessment.data.jpa.users.spaceinvitee.SpaceInviteeJpaEntity;
import org.flickit.assessment.data.jpa.users.spaceinvitee.SpaceInviteeJpaRepository;
import org.springframework.stereotype.Component;

@Component("coreSpaceInviteePersistenceJpaAdapter")
@RequiredArgsConstructor
public class SpaceInviteePersistenceJpaAdapter implements InviteSpaceMemberPort {

    private final SpaceInviteeJpaRepository repository;

    @Override
    public void invite(Param param) {
        var entity = new SpaceInviteeJpaEntity(null,
            param.spaceId(),
            param.email(),
            param.createdBy(),
            param.creationTime(),
            param.expirationDate());
        repository.save(entity);
    }
}
