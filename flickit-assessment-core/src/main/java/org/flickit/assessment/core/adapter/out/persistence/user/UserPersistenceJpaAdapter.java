package org.flickit.assessment.core.adapter.out.persistence.user;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.User;
import org.flickit.assessment.core.application.port.out.user.LoadUserEmailByUserIdPort;
import org.flickit.assessment.core.application.port.out.user.LoadUserPort;
import org.flickit.assessment.data.jpa.users.user.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.USER_ID_NOT_FOUND;

@Component("coreUserPersistenceJpaAdapter")
@RequiredArgsConstructor
public class UserPersistenceJpaAdapter implements
    LoadUserPort,
    LoadUserEmailByUserIdPort {

    private final UserJpaRepository repository;

    @Override
    public Optional<User> loadById(UUID userId) {
        return repository.findById(userId).map(UserMapper::mapToDomainModel);
    }

    @Override
    public Optional<User> loadByEmail(String email) {
        return repository.findByEmail(email).map(UserMapper::mapToDomainModel);
    }

    @Override
    public String loadEmail(UUID userId) {
        return repository.findEmailByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException(USER_ID_NOT_FOUND));
    }
}
