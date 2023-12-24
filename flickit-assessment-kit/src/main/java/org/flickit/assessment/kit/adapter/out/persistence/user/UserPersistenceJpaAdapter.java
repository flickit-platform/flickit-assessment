package org.flickit.assessment.kit.adapter.out.persistence.user;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.user.UserJpaRepository;
import org.flickit.assessment.kit.application.domain.User;
import org.flickit.assessment.kit.application.port.out.user.LoadUserByEmailPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserPersistenceJpaAdapter implements LoadUserByEmailPort {

    private final UserJpaRepository repository;

    @Override
    public Optional<User> loadByEmail(String email) {
        return repository.findByEmailIgnoreCase(email).map(UserMapper::mapToDomainModel);
    }
}
