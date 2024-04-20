package org.flickit.assessment.users.adapter.out.persistence.user;

import org.flickit.assessment.data.jpa.users.user.UserJpaEntity;
import org.flickit.assessment.data.jpa.users.user.UserJpaRepository;
import org.flickit.assessment.users.application.port.out.user.LoadUserEmailByUserIdPort;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.users.application.port.out.user.LoadUserIdByEmailPort;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserPersistenceJpaAdapter implements
    LoadUserEmailByUserIdPort,
    LoadUserIdByEmailPort {

    private final UserJpaRepository repository;

    @Override
    public String loadEmail(UUID userId) {
        return repository.findEmailByUserId(userId) ;
    }

    @Override
    public UUID loadByEmail(String email) {
        var entity = repository.findByEmailIgnoreCase (email);
        return entity.map(UserJpaEntity::getId).orElse(null);
    }
}

