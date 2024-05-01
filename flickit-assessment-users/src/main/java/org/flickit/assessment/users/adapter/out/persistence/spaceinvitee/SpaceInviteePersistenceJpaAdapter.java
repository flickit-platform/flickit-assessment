package org.flickit.assessment.users.adapter.out.persistence.spaceinvitee;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.data.jpa.users.spaceinvitee.SpaceInviteeJpaEntity;
import org.flickit.assessment.data.jpa.users.spaceinvitee.SpaceInviteeJpaRepository;
import org.flickit.assessment.users.application.domain.SpaceInvitation;
import org.flickit.assessment.users.application.port.in.spaceinvitee.GetSpaceUserInvitationsPort;
import org.flickit.assessment.users.application.port.out.spaceinvitee.DeleteSpaceUserInvitationsPort;
import org.flickit.assessment.users.application.port.out.spaceinvitee.LoadSpaceInviteesPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.InviteSpaceMemberPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SpaceInviteePersistenceJpaAdapter implements
    GetSpaceUserInvitationsPort,
    DeleteSpaceUserInvitationsPort,
    InviteSpaceMemberPort,
    LoadSpaceInviteesPort {

    private final SpaceInviteeJpaRepository repository;

    @Override
    public void deleteAll(String email) {
        repository.deleteByEmail(email);
    }

    @Override
    public List<SpaceInvitation> loadInvitations(String email) {
        var invitations = repository.findByEmail(email.toLowerCase());
        return invitations
            .stream()
            .map(SpaceInviteeMapper::mapToDomain)
            .toList();
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

    @Override
    public PaginatedResponse<Invitee> loadInvitees(long spaceId, int page, int size) {
        var pageResult = repository.findBySpaceId(spaceId, LocalDateTime.now(),
            PageRequest.of(page, size, Sort.Direction.DESC, SpaceInviteeJpaEntity.Fields.CREATION_TIME));

        var items = pageResult
            .stream()
            .map(SpaceInviteeMapper::mapToInvitee)
            .toList();

        return new PaginatedResponse<>(
            items,
            pageResult.getNumber(),
            pageResult.getSize(),
            SpaceInviteeJpaEntity.Fields.CREATION_TIME,
            Sort.Direction.DESC.name().toLowerCase(),
            (int) pageResult.getTotalElements()
        );
    }
}
