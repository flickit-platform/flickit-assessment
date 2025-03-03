package org.flickit.assessment.users.adapter.out.persistence.space;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.common.exception.ResourceAlreadyExistsException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.users.space.SpaceJpaRepository;
import org.flickit.assessment.data.jpa.users.spaceuseraccess.SpaceUserAccessJpaEntity;
import org.flickit.assessment.users.application.domain.Space;
import org.flickit.assessment.users.application.port.out.space.*;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.UpdateSpaceLastSeenPort;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.users.adapter.out.persistence.space.SpaceMapper.mapToDomain;
import static org.flickit.assessment.users.common.ErrorMessageKey.CREATE_SPACE_TITLE_DUPLICATE;
import static org.flickit.assessment.users.common.ErrorMessageKey.SPACE_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class SpacePersistenceJpaAdapter implements
    CreateSpacePort,
    LoadSpaceListPort,
    LoadSpaceOwnerPort,
    LoadSpaceDetailsPort,
    UpdateSpaceLastSeenPort,
    CountSpaceAssessmentPort,
    DeleteSpacePort,
    UpdateSpacePort,
    CountSpacesPort {

    private final SpaceJpaRepository repository;
    private final EntityManager entityManager;

    @Override
    public PaginatedResponse<LoadSpaceListPort.Result> loadSpaceList(UUID currentUserId, int page, int size) {
        var pageResult = repository.findByUserId(currentUserId, PageRequest.of(page, size));

        List<LoadSpaceListPort.Result> items = pageResult.getContent().stream()
            .map(entity -> new LoadSpaceListPort.Result(
                mapToDomain(entity.getSpace()),
                entity.getOwnerName(),
                entity.getMembersCount(),
                entity.getAssessmentsCount()))
            .toList();

        return new PaginatedResponse<>(
            items,
            pageResult.getNumber(),
            pageResult.getSize(),
            SpaceUserAccessJpaEntity.Fields.lastSeen,
            Sort.Direction.DESC.name().toLowerCase(),
            (int) pageResult.getTotalElements()
        );
    }

    @Override
    public long persist(Space space) {
        var unsavedEntity = SpaceMapper.mapToJpaEntity(space);
        try {
            var savedEntity = repository.save(unsavedEntity);
            entityManager.flush();
            return savedEntity.getId();
        } catch (ConstraintViolationException e) {
            throw new ResourceAlreadyExistsException(CREATE_SPACE_TITLE_DUPLICATE);
        }
    }

    @Override
    public UUID loadOwnerId(long spaceId) {
        return repository.loadOwnerIdById(spaceId)
            .orElseThrow(() -> new ResourceNotFoundException(SPACE_ID_NOT_FOUND));
    }

    @Override
    public LoadSpaceDetailsPort.Result loadSpace(long spaceId) {
        var entity = repository.loadSpaceDetails(spaceId)
            .orElseThrow(() -> new ResourceNotFoundException(SPACE_ID_NOT_FOUND));
        return new LoadSpaceDetailsPort.Result(
            mapToDomain(entity.getSpace()),
            entity.getMembersCount(),
            entity.getAssessmentsCount());
    }

    @Override
    public void updateLastSeen(long spaceId, UUID userId, LocalDateTime currentTime) {
        repository.updateLastSeen(spaceId, userId, currentTime);
    }

    @Override
    public int countAssessments(long spaceId) {
        return repository.countAssessments(spaceId);
    }

    @Override
    public void deleteById(long spaceId, long deletionTime) {
        if (!repository.existsByIdAndDeletedFalse(spaceId))
            throw new ResourceNotFoundException(SPACE_ID_NOT_FOUND);
        repository.delete(spaceId, deletionTime);
    }

    @Override
    public void updateSpace(Param param) {
        repository.update(param.id(), param.title(), param.code(), param.lastModificationTime(), param.lastModifiedBy());
    }

    @Override
    public int countBasicSpaces(UUID ownerId) {
        return repository.countByOwnerIdAndTypeAndDeletedFalse(ownerId, SpaceType.BASIC.getId());
    }
}
