package org.flickit.assessment.users.adapter.out.persistence.user;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.users.user.UserJpaRepository;
import org.flickit.assessment.users.application.port.out.user.LoadUserEmailByUserIdPort;
import org.flickit.assessment.users.application.port.out.user.LoadUserPort;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.flickit.assessment.users.common.ErrorMessageKey.USER_BY_EMAIL_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class UserPersistenceJpaAdapter implements
    LoadUserEmailByUserIdPort,
    LoadUserPort {

    private final UserJpaRepository repository;

    @Override
    public String loadEmail(UUID userId) {
        return repository.findEmailByUserId(userId);
    }

    @Override
    public UUID loadUserIdByEmail(String email) {
        return repository.findUserIdByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException(USER_BY_EMAIL_NOT_FOUND));
    }
}

