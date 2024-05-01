package org.flickit.assessment.users.adapter.out.persistence.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.users.space.SpaceJpaRepository;
import org.flickit.assessment.users.application.domain.Space;
import org.flickit.assessment.users.application.port.out.space.CreateSpacePort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpacePersistenceJpaAdapter implements CreateSpacePort {

    private final SpaceJpaRepository repository;

    @Override
    public long persist(Space space) {
        var unsavedEntity = SpaceMapper.mapToJpaEntity(space);
        var savedEntity = repository.save(unsavedEntity);
        return savedEntity.getId();
    }
}
