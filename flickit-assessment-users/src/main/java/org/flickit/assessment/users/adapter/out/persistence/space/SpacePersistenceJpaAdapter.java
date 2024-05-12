package org.flickit.assessment.users.adapter.out.persistence.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.users.space.SpaceJpaRepository;
import org.flickit.assessment.users.application.domain.Space;
import org.flickit.assessment.users.application.port.out.LoadSpaceDetailsPort;
import org.flickit.assessment.users.application.port.out.space.CreateSpacePort;
import org.flickit.assessment.users.application.port.out.space.LoadSpaceOwnerPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.UpdateSpaceLastSeenPort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.users.adapter.out.persistence.space.SpaceMapper.mapToDomain;
import static org.flickit.assessment.users.common.ErrorMessageKey.SPACE_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class SpacePersistenceJpaAdapter implements
    CreateSpacePort,
    LoadSpaceOwnerPort,
    LoadSpaceDetailsPort,
    UpdateSpaceLastSeenPort {

    private final SpaceJpaRepository repository;

    @Override
    public long persist(Space space) {
        var unsavedEntity = SpaceMapper.mapToJpaEntity(space);
        var savedEntity = repository.save(unsavedEntity);
        return savedEntity.getId();
    }

    @Override
    public UUID loadOwnerId(long id) {
        return repository.loadOwnerIdById(id)
            .orElseThrow(() -> new ResourceNotFoundException(SPACE_ID_NOT_FOUND));
    }

    @Override
    public LoadSpaceDetailsPort.Result loadSpace(long id, UUID currentUserId) {
        var entity = repository.findById(id, currentUserId)
            .orElseThrow(() -> new ResourceNotFoundException(SPACE_ID_NOT_FOUND));
        return new LoadSpaceDetailsPort.Result(
            mapToDomain(entity.getSpace()),
            entity.getMembersCount(),
            entity.getAssessmentsCount());
    }

    @Override
    public void updateLastSeen(long spaceId, UUID userId, LocalDateTime currentTime) {
        repository.updateLastSeen(spaceId, currentTime, userId);}
}
