package org.flickit.assessment.kit.adapter.out.persistence.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.user.UserJpaEntity;
import org.flickit.assessment.kit.application.domain.User;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitUserListUseCase;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static User mapToDomainModel(UserJpaEntity entity) {
        return new User(
            entity.getId(),
            entity.getEmail(),
            entity.getDisplayName(),
            entity.getBio(),
            entity.getLinkedin(),
            entity.getPicture(),
            entity.getDefaultSpaceId()
        );
    }

    public static GetKitUserListUseCase.UserListItem mapToUserListItem(UserJpaEntity entity) {
        return new GetKitUserListUseCase.UserListItem(
            entity.getDisplayName(),
            entity.getEmail());
    }
}
