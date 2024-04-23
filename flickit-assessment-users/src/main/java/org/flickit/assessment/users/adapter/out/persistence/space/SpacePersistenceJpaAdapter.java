package org.flickit.assessment.users.adapter.out.persistence.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.users.space.SpaceJpaRepository;
import org.flickit.assessment.users.application.port.out.space.CreateSpacePort;

@RequiredArgsConstructor
public class SpacePersistenceJpaAdapter implements CreateSpacePort {

    private final SpaceJpaRepository repository;

    @Override
    public void persist(Param param) {
        var entity = SpaceMapper.mapCreateParamToJpaEntity(param);
        repository.save(entity);
    }
}
