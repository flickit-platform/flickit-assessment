package org.flickit.assessment.kit.adapter.out.persistence.users.user;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.users.user.UserJpaRepository;
import org.flickit.assessment.kit.application.domain.User;
import org.flickit.assessment.kit.application.port.out.user.LoadUserPort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component("kitUserPersistenceJpaAdapter")
@RequiredArgsConstructor
public class UserPersistenceJpaAdapter implements
    LoadUserPort {

    private final UserJpaRepository repository;

    @Override
    public Optional<User> loadById(UUID userId) {
        return repository.findById(userId).map(UserMapper::mapToDomainModel);
    }
}
