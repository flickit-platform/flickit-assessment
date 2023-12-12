package org.flickit.assessment.core.adapter.out.persistence.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.User;
import org.flickit.assessment.data.jpa.kit.user.UserJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static UserJpaEntity toJpaEntity(User user) {
        return new UserJpaEntity(
            user.getId(),
            user.getEmail(),
            user.getDisplayName(),
            user.getBio(),
            user.getLastLogin(),
            user.isSuperUser(),
            user.isStaff(),
            user.isActive(),
            user.getCurrentSpaceId(),
            user.getDefaultSpaceId()
        );
    }
}
