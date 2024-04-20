package org.flickit.assessment.users.adapter.out.persistence.spaceinvitee;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.users.spaceinvitee.SpaceInviteeJpaRepository;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.SaveSpaceMemberInviteePort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpaceInviteePersistenceJpaEntity implements
    SaveSpaceMemberInviteePort {

    private final SpaceInviteeJpaRepository repository;

    @Override
    public void persist(SaveSpaceMemberInviteePort.Param param) {
        var entity = SpaceInviteeMapper.mapCreateParamToJpaEntity(param);
        boolean isExist = repository.existsBySpaceIdAndEmail(param.spaceId(), param.inviteeMail());
        if (isExist)
            repository.update(param.spaceId(), param.inviteeMail(), param.inviteDate(),
                param.inviteExpirationDate(), param.inviterId());
        else
            repository.save(entity);

    }
}
