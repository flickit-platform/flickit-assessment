package org.flickit.assessment.users.adapter.out.persistence.spaceinvitee;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.users.spaceinvitee.SpaceInviteeJpaEntity;
import org.flickit.assessment.data.jpa.users.spaceinvitee.SpaceInviteeJpaRepository;
import org.flickit.assessment.users.application.port.in.spaceinvitee.LoadUserInvitedSpacesPort;
import org.flickit.assessment.users.application.port.out.spaceinvitee.DeleteSpaceUserInvitations;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class SpaceInviteePersistenceJpaAdapter implements
    LoadUserInvitedSpacesPort,
    DeleteSpaceUserInvitations {

    private final SpaceInviteeJpaRepository repository;

    @Override
    public void delete(String email) {
        repository.deleteByEmail(email);
    }

    @Override
    public List<Result> loadSpaces(String email) {
        var invitee = repository.findByEmail(email);
        return invitee
            .stream()
            .map(SpaceInviteePersistenceJpaAdapter::mapInviteeEntityToPortResult)
            .toList();
    }

    private static Result mapInviteeEntityToPortResult(SpaceInviteeJpaEntity entities) {
        return new Result(entities.getSpaceId(), entities.getCreatedBy());
    }
}
