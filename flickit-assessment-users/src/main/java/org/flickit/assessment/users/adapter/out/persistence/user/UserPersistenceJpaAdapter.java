package org.flickit.assessment.users.adapter.out.persistence.user;

import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.data.jpa.users.user.UserJpaRepository;
import org.flickit.assessment.users.application.port.out.user.LoadUserEmailByUserIdPort;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.users.application.port.out.user.LoadUserIdByEmailPort;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.flickit.assessment.users.common.ErrorMessageKey.ADD_SPACE_MEMBER_USER_ID_NOT_FOUND;

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
        UUID userId = repository.findUserIdByEmail(email);
        if (userId == null)
            throw new ValidationException(ADD_SPACE_MEMBER_USER_ID_NOT_FOUND);
        return userId;
    }
}

