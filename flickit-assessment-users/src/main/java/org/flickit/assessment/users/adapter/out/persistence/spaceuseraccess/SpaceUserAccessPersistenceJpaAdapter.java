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
import org.flickit.assessment.users.application.port.out.spaceuseraccess.DeleteSpaceMemberPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.LoadSpaceMembersPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.users.common.ErrorMessageKey.SPACE_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class SpaceUserAccessPersistenceJpaAdapter implements
    CreateSpaceUserAccessPort,
    CheckSpaceAccessPort,
    DeleteSpaceMemberPort,
    LoadSpaceMembersPort {

    private final SpaceUserAccessJpaRepository repository;
    private final SpaceJpaRepository spaceRepository;

    @Override
    public void persist(SpaceUserAccess access) {
        SpaceUserAccessJpaEntity unsavedEntity = new SpaceUserAccessJpaEntity(access.getSpaceId(), access.getUserId(),
            access.getCreatedBy(), access.getCreationTime(), access.getCreationTime());
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

        return repository.existsBySpaceIdAndUserId(spaceId, userId);
    }

    @Override
    public PaginatedResponse<Member> loadSpaceMembers(long spaceId, int page, int size) {
        var spaceOwnerId = spaceRepository.loadOwnerIdById(spaceId)
            .orElseThrow(() -> new ResourceNotFoundException(SPACE_ID_NOT_FOUND));
        var pageResult = repository.findMembers(spaceId,
            PageRequest.of(page, size, Sort.Direction.DESC, SpaceUserAccessJpaEntity.Fields.LAST_SEEN));

        var items = pageResult.getContent()
            .stream()
            .map(e -> mapToResult(e, spaceOwnerId))
            .toList();

        return new PaginatedResponse<>(
            items,
            pageResult.getNumber(),
            pageResult.getSize(),
            SpaceUserAccessJpaEntity.Fields.LAST_SEEN,
            Sort.Direction.DESC.name().toLowerCase(),
            (int) pageResult.getTotalElements()
        );
    }

    private static Member mapToResult(SpaceMembersView view, UUID spaceOwnerId) {
        return new Member(
            view.getId(),
            view.getEmail(),
            view.getDisplayName(),
            view.getBio(),
            view.getId().equals(spaceOwnerId),
            view.getPicture(),
            view.getLinkedin());
    }

    @Override
    public void delete(long spaceId, UUID userId) {
        repository.deleteBySpaceIdAndUserId(spaceId, userId);
    }
}
