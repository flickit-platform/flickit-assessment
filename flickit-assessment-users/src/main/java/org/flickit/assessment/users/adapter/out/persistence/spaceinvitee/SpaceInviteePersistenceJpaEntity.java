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
        var entity = SpaceInviteeMapper.mapCreateParamToJpaEntity(param);
        if (!repository.existsBySpaceIdAndEmail(param.spaceId(), param.email()))
            repository.save(entity);
        else
            repository.update(entity.getId(), param.creationTime(), param.expirationDate(), param.createdBy());
    }
}
