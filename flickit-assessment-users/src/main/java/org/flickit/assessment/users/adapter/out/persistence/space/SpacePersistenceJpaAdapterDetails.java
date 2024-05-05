package org.flickit.assessment.users.adapter.out.persistence.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.domain.Space;
import org.flickit.assessment.users.application.port.out.LoadSpaceDetailsPort;
import org.flickit.assessment.data.jpa.users.space.SpaceJpaRepository;
import org.flickit.assessment.users.application.port.out.space.CreateSpacePort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.UpdateSpaceLastSeenPort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.users.adapter.out.persistence.space.SpaceMapper.mapToPortResult;
import static org.flickit.assessment.users.common.ErrorMessageKey.SPACE_ID_NOT_FOUND;


@Component
@RequiredArgsConstructor
public class SpacePersistenceJpaAdapterDetails implements
    LoadSpaceDetailsPort,
    UpdateSpaceLastSeenPort,
    CreateSpacePort {

    private final SpaceJpaRepository repository;

    @Override
    public Result loadSpace(long id, UUID currentUserId) {
        var entity = repository.findById(id, currentUserId)
            .orElseThrow(() -> new ResourceNotFoundException(SPACE_ID_NOT_FOUND));
        return mapToPortResult(entity);
    }

    @Override
    public void updateLastSeen(long spaceId, LocalDateTime currentTime, UUID currentUserId) {
        repository.updateLastSeen(spaceId, currentTime, currentUserId);}

    @Override
    public long persist(Space space) {
        var unsavedEntity = SpaceMapper.mapToJpaEntity(space);
        var savedEntity = repository.save(unsavedEntity);
        return savedEntity.getId();
    }
}
