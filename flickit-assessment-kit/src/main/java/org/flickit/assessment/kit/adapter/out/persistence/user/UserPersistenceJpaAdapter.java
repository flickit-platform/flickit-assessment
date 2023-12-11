package org.flickit.assessment.kit.adapter.out.persistence.user;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.user.UserJpaRepository;
import org.flickit.assessment.kit.application.port.out.user.DeleteUserAccessPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserPersistenceJpaAdapter implements DeleteUserAccessPort {

    private final UserJpaRepository repository;

    @Override
    public void delete(Long kitId, Long userId) {
        repository.deleteByKitIdAndUserId(kitId, userId);
    }
}
