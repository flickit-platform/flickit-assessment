package org.flickit.assessment.core.adapter.out.persistence.user;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.domain.User;
import org.flickit.assessment.core.application.port.out.user.LoadUserPort;
import org.flickit.assessment.data.jpa.users.user.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component("coreUserPersistenceJpaAdapter")
@RequiredArgsConstructor
public class UserPersistenceJpaAdapter implements LoadUserPort {

    private final UserJpaRepository repository;

    @Override
    public Optional<User> loadById(UUID userId) {
        return repository.findById(userId).map(UserMapper::mapToDomainModel);
    }
}
