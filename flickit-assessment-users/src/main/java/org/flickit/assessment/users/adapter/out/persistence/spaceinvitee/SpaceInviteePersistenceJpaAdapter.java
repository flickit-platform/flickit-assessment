package org.flickit.assessment.users.adapter.out.persistence.spaceinvitee;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.users.spaceinvitee.SpaceInviteeJpaEntity;
import org.flickit.assessment.data.jpa.users.spaceinvitee.SpaceInviteeJpaRepository;
import org.flickit.assessment.users.application.port.in.spaceinvitee.LoadSpaceUserInvitationsPort;
import org.flickit.assessment.users.application.port.out.spaceinvitee.DeleteSpaceUserInvitations;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class SpaceInviteePersistenceJpaAdapter implements
    LoadSpaceUserInvitationsPort,
    DeleteSpaceUserInvitations {

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

    private static Invitation mapInviteeEntityToPortResult(SpaceInviteeJpaEntity entities) {
        return new Invitation(entities.getSpaceId(), entities.getCreatedBy());
    }
}
