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
        boolean isExist = repository.existsBySpaceIdAndEmail(param.spaceId(), param.email());
        if (isExist)
            repository.update(param.spaceId(), param.email(), param.creationTime(),
                param.expirationDate(), param.createdBy());
        else
            repository.save(entity);

    }
}
