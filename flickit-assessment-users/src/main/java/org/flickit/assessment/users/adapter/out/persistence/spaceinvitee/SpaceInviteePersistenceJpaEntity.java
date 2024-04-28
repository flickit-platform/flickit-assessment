package org.flickit.assessment.users.adapter.out.persistence.spaceinvitee;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.users.spaceinvitee.SpaceInviteeJpaRepository;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.InviteSpaceMemberPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpaceInviteePersistenceJpaEntity implements
    InviteSpaceMemberPort {

    private final SpaceInviteeJpaRepository repository;

    @Override
    public void invite(InviteSpaceMemberPort.Param param) {
        String email = param.email().toLowerCase();
        var entity = SpaceInviteeMapper.mapCreateParamToJpaEntity(param);
        if (!repository.existsBySpaceIdAndEmail(param.spaceId(), email))
            repository.save(entity);
        else
            repository.update(param.spaceId(), email, param.creationTime(), param.expirationDate(), param.createdBy());
    }
}
