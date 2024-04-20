package org.flickit.assessment.users.adapter.out.persistence.spaceuseraccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.users.user.UserJpaRepository;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CheckSpaceMemberAccessPort;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SpaceUserAccessPersistenceJpaAdapter implements
    CheckSpaceMemberAccessPort {

    private final UserJpaRepository userRepository;

    @Override
    public boolean checkIsMember(UUID userId) {
        return userRepository.existsById(userId);
    }

}
