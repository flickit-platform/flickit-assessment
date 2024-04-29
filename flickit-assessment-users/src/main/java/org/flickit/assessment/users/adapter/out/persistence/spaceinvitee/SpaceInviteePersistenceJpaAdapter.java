package org.flickit.assessment.users.adapter.out.persistence.spaceinvitee;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.users.spaceinvitee.SpaceInviteeJpaEntity;
import org.flickit.assessment.data.jpa.users.spaceinvitee.SpaceInviteeJpaRepository;
import org.flickit.assessment.users.application.port.in.spaceinvitee.LoadSpaceUserInvitationsPort;
import org.flickit.assessment.users.application.port.out.spaceinvitee.DeleteSpaceUserInvitationsPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.InviteSpaceMemberPort;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class SpaceInviteePersistenceJpaAdapter implements
    LoadSpaceUserInvitationsPort,
    DeleteSpaceUserInvitationsPort,
    InviteSpaceMemberPort {

    private final SpaceInviteeJpaRepository repository;

    @Override
    public void delete(String email) {
        repository.deleteByEmail(email);
    }

    @Override
    public List<Invitation> loadInvitations(String email) {
        var invitee = repository.findByEmail(email);
        return invitee
            .stream()
            .map(SpaceInviteePersistenceJpaAdapter::mapInviteeEntityToPortResult)
            .toList();
    }

    private static Invitation mapInviteeEntityToPortResult(SpaceInviteeJpaEntity entity) {
        return new Invitation(entity.getSpaceId(), entity.getExpirationDate(), entity.getCreatedBy());
    }

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
