package org.flickit.assessment.core.adapter.out.persistence.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.FullUser;
import org.flickit.assessment.core.application.domain.User;
import org.flickit.assessment.data.jpa.users.user.UserJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static User mapToDomainModel(UserJpaEntity entity) {
        return new User(entity.getId(), entity.getDisplayName());
    }

    public static FullUser mapToFullDomain(UserJpaEntity entity) {
        return new FullUser(entity.getId(), entity.getDisplayName(), entity.getEmail(), entity.getPicture());
    }
}
