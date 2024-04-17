package org.flickit.assessment.users.adapter.out.persistence.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.users.application.port.out.space.CheckSpaceExistencePort;
import org.flickit.assessment.data.jpa.users.space.SpaceJpaRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpacePersistenceJpaAdapter implements
    CheckSpaceExistencePort {

    private final SpaceJpaRepository repository;

    @Override
    public boolean existsById(long id) {
        return repository.existsById(id);
    }
}
