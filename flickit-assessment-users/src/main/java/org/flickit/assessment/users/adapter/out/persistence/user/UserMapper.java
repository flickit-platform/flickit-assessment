package org.flickit.assessment.users.adapter.out.persistence.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.users.user.UserJpaEntity;
import org.flickit.assessment.users.application.domain.User;

import java.time.LocalDateTime;
import java.util.UUID;

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

    public static UserJpaEntity mapToJpaEntity(UUID id, String email, String displayName, LocalDateTime creationTime, LocalDateTime lastModificationTime) {
        return new UserJpaEntity(
            id,
            email,
            displayName,
            creationTime,
            lastModificationTime);
    }
}
