package org.flickit.assessment.kit.application.port.out.user;

import org.flickit.assessment.data.jpa.kit.user.UserJpaEntity;

import java.util.Optional;

public interface LoadUserListByExpertGroupUseCase {

    Optional<UserJpaEntity> UsersOfExpertGroups();
}
