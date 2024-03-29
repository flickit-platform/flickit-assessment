package org.flickit.assessment.users.adapter.out.persistence.user;

import org.flickit.assessment.data.jpa.users.user.UserJpaRepository;
import org.flickit.assessment.users.application.port.out.user.LoadUserEmailByUserIdPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserPersistenceJpaAdapter implements
    LoadUserEmailByUserIdPort {

    private final UserJpaRepository repository;

    @Override
    public String loadEmail(UUID userId) {
        return repository.findEmailByUserId(userId) ;
    }
}

