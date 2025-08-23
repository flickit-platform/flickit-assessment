package org.flickit.assessment.users.adapter.out.persistence.spaceinvitee;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.users.space.SpaceJpaRepository;
import org.flickit.assessment.data.jpa.users.spaceinvitee.SpaceInviteeJpaEntity;
import org.flickit.assessment.data.jpa.users.spaceinvitee.SpaceInviteeJpaRepository;
import org.flickit.assessment.users.application.domain.SpaceInvitee;
import org.flickit.assessment.users.application.port.out.spaceinvitee.*;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.InviteSpaceMemberPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.users.common.ErrorMessageKey.SPACE_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class SpaceInviteePersistenceJpaAdapter implements
    LoadSpaceUserInvitationsPort,
    DeleteSpaceUserInvitationsPort,
    InviteSpaceMemberPort,
    LoadSpaceInviteesPort,
    LoadSpaceInvitationPort,
    DeleteSpaceInvitationPort {

    private final SpaceInviteeJpaRepository repository;
    private final SpaceJpaRepository spaceRepository;

    @Override
    public void deleteAll(String email) {
        repository.deleteByEmail(email);
    }

    @Override
    public List<SpaceInvitee> loadInvitations(String email) {
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
    public PaginatedResponse<SpaceInvitee> loadInvitees(long spaceId, int page, int size) {
        if (!spaceRepository.existsByIdAndDeletedFalse(spaceId))
            throw new ResourceNotFoundException(SPACE_ID_NOT_FOUND);

        var pageResult = repository.findBySpaceId(spaceId, LocalDateTime.now(),
            PageRequest.of(page, size, Sort.Direction.DESC, SpaceInviteeJpaEntity.Fields.creationTime));

        var items = pageResult
            .stream()
            .map(SpaceInviteeMapper::mapToDomain)
            .toList();

        return new PaginatedResponse<>(
            items,
            pageResult.getNumber(),
            pageResult.getSize(),
            SpaceInviteeJpaEntity.Fields.creationTime,
            Sort.Direction.DESC.name().toLowerCase(),
            (int) pageResult.getTotalElements()
        );
    }

    @Override
    public Optional<SpaceInvitee> loadSpaceInvitation(UUID id) {
        return repository.findById(id).map(SpaceInviteeMapper::mapToDomain);
    }

    @Override
    public void deleteSpaceInvitation(UUID inviteId) {
        repository.deleteById(inviteId);
    }
}
