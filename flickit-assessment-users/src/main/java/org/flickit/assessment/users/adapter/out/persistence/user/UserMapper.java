package org.flickit.assessment.users.adapter.out.persistence.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.users.user.UserJpaEntity;
import org.flickit.assessment.users.application.domain.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static User mapToDomainModel(UserJpaEntity userEntity) {
        return new User(userEntity.getId(),
            userEntity.getEmail(),
            userEntity.getDisplayName(),
            userEntity.getBio(),
            userEntity.getLinkedin(),
            userEntity.getPicture());
    }
}
