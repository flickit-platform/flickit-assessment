package org.flickit.assessment.kit.adapter.out.persistence.users.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.users.user.UserJpaEntity;
import org.flickit.assessment.kit.application.domain.User;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitUsersPort;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static User mapToDomainModel(UserJpaEntity entity) {
        return new User(entity.getId());
    }

    public static LoadKitUsersPort.KitUser mapToUserListItem(UserJpaEntity entity) {
        return new LoadKitUsersPort.KitUser(
            entity.getId(),
            entity.getDisplayName(),
            entity.getEmail(),
            entity.getPicture());
    }
}
