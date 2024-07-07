package org.flickit.assessment.users.adapter.out.persistence.user;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.users.user.UserJpaEntity;
import org.flickit.assessment.data.jpa.users.user.UserJpaRepository;
import org.flickit.assessment.users.application.domain.User;
import org.flickit.assessment.users.application.port.out.user.CreateUserPort;
import org.flickit.assessment.users.application.port.out.user.LoadUserEmailByUserIdPort;
import org.flickit.assessment.users.application.port.out.user.LoadUserPort;
import org.flickit.assessment.users.application.port.out.user.UpdateUserPort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.users.common.ErrorMessageKey.USER_BY_EMAIL_NOT_FOUND;
import static org.flickit.assessment.users.common.ErrorMessageKey.USER_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class UserPersistenceJpaAdapter implements
    LoadUserEmailByUserIdPort,
    LoadUserPort,
    CreateUserPort,
    UpdateUserPort {

    private final UserJpaRepository repository;

    @Override
    public String loadEmail(UUID userId) {
        return repository.findEmailByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException(USER_ID_NOT_FOUND));
    }

    @Override
    public Optional<UUID> loadUserIdByEmail(String email) {
        return repository.findUserIdByEmail(email.toLowerCase());
    }

    @Override
    public User loadUser(UUID id) {
        UserJpaEntity userEntity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(USER_ID_NOT_FOUND));

        return UserMapper.mapToDomainModel(userEntity);
    }

    @Override
    public LoadUserPort.Result loadFullUserByEmail(String email) {
        UserJpaEntity userEntity = repository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException(USER_BY_EMAIL_NOT_FOUND));

        User user = UserMapper.mapToDomainModel(userEntity);
        return new LoadUserPort.Result(user,
            userEntity.getLastLogin(),
            userEntity.getIsSuperUser(),
            userEntity.getIsStaff(),
            userEntity.getIsActive(),
            userEntity.getPassword());
    }

    @Override
    public UUID persist(UUID id, String displayName, String email) {
        UserJpaEntity userEntity = UserMapper.mapToJpaEntity(id, email, displayName);

        return repository.save(userEntity).getId();
    }

    @Override
    public void updateUser(UpdateUserPort.Param param) {
        if (!repository.existsById(param.userId()))
            throw new ResourceNotFoundException(USER_ID_NOT_FOUND);

        repository.update(param.userId(), param.displayName(), param.bio(), param.linkedin());
    }
}

