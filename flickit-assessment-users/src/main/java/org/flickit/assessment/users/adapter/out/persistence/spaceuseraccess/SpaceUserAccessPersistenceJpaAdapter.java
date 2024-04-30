package org.flickit.assessment.users.adapter.out.persistence.spaceuseraccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.users.space.SpaceJpaRepository;
import org.flickit.assessment.data.jpa.users.spaceuseraccess.SpaceMembersView;
import org.flickit.assessment.data.jpa.users.spaceuseraccess.SpaceUserAccessJpaEntity;
import org.flickit.assessment.data.jpa.users.spaceuseraccess.SpaceUserAccessJpaRepository;
import org.flickit.assessment.users.application.domain.SpaceUserAccess;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CreateSpaceUserAccessPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.LoadSpaceMembersPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.LoadSpaceMembersPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.users.common.ErrorMessageKey.SPACE_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class SpaceUserAccessPersistenceJpaAdapter implements
    CreateSpaceUserAccessPort,
    CheckSpaceAccessPort,
    LoadSpaceMembersPort {

    private final SpaceUserAccessJpaRepository repository;
    private final SpaceJpaRepository spaceRepository;

    @Override
    public void persist(SpaceUserAccess access) {
        SpaceUserAccessJpaEntity unsavedEntity = new SpaceUserAccessJpaEntity(access.getSpaceId(), access.getUserId(),
            access.getCreatedBy(), access.getCreationTime());
        repository.save(unsavedEntity);
    }

    @Override
    public void persistAll(List<SpaceUserAccess> param) {
        List<SpaceUserAccessJpaEntity> entities = param
            .stream()
            .map(SpaceUserAccessMapper::mapToJpaEntity).toList();

        repository.saveAll(entities);
    }

    @Override
    public boolean checkIsMember(long spaceId, UUID userId) {
        if (!spaceRepository.existsById(spaceId))
            throw new ResourceNotFoundException(SPACE_ID_NOT_FOUND);

        return repository.existsByUserIdAndSpaceId(userId, spaceId);
    }

    @Override
    public PaginatedResponse<Member> loadSpaceMembers(long spaceId, int page, int size) {
        var pageResult = repository.findMembers(spaceId,
            PageRequest.of(page, size, Sort.Direction.DESC, SpaceUserAccessJpaEntity.Fields.CREATION_TIME));

        var items = pageResult.getContent()
            .stream()
            .map(SpaceUserAccessPersistenceJpaAdapter::mapToResult)
            .toList();

        return new PaginatedResponse<>(
            items ,
            pageResult.getNumber(),
            pageResult.getSize(),
            SpaceUserAccessJpaEntity.Fields.CREATION_TIME,
            Sort.Direction.DESC.name().toLowerCase(),
            (int) pageResult.getTotalElements()
        );
    }

    private static Member mapToResult(SpaceMembersView view) {
        return new Member(
            view.getId(),
            view.getEmail(),
            view.getDisplayName(),
            view.getBio(),
            view.getPicture(),
            view.getLinkedin());
    }
}
