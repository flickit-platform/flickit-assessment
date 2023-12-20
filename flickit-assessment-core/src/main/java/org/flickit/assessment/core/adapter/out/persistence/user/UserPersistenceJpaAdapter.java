package org.flickit.assessment.core.adapter.out.persistence.user;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.out.user.CheckUserExistencePort;
import org.flickit.assessment.data.jpa.kit.user.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserPersistenceJpaAdapter implements CheckUserExistencePort {

    private final UserJpaRepository repository;


    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }
}
