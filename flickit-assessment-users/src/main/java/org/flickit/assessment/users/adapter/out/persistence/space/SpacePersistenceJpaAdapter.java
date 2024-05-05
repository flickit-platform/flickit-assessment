package org.flickit.assessment.users.adapter.out.persistence.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.users.space.SpaceJpaRepository;
import org.flickit.assessment.users.application.domain.Space;
import org.flickit.assessment.users.application.port.out.space.CreateSpacePort;
import org.flickit.assessment.users.application.port.out.space.LoadSpacePort;
import org.springframework.stereotype.Component;

import static org.flickit.assessment.users.common.ErrorMessageKey.SPACE_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class SpacePersistenceJpaAdapter implements
    CreateSpacePort,
    LoadSpacePort {

    private final SpaceJpaRepository repository;

    @Override
    public long persist(Space space) {
        var unsavedEntity = SpaceMapper.mapToJpaEntity(space);
        var savedEntity = repository.save(unsavedEntity);
        return savedEntity.getId();
    }

    @Override
    public Space loadSpace(long id) {
        var entity = repository.findById(id)
            .orElseThrow(()-> new  ResourceNotFoundException(SPACE_ID_NOT_FOUND));
        return SpaceMapper.mapJpaToDomain(entity);
    }
}
