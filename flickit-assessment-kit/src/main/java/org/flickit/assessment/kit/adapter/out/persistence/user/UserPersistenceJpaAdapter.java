package org.flickit.assessment.kit.adapter.out.persistence.user;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.user.UserJpaRepository;
import org.flickit.assessment.kit.application.domain.User;
import org.flickit.assessment.kit.application.port.out.user.DeleteUserAccessPort;
import org.flickit.assessment.kit.application.port.out.user.LoadUserByIdPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserPersistenceJpaAdapter implements
    DeleteUserAccessPort,
    LoadUserByIdPort {

    private final UserJpaRepository repository;

    @Override
    public void delete(Long kitId, Long userId) {
        repository.deleteByKitIdAndUserId(kitId, userId);
    }

    @Override
    public Optional<User> load(Long userId) {
        var entity = repository.findById(userId);
        return entity.map(UserMapper::mapToDomainModel);
    }
}
