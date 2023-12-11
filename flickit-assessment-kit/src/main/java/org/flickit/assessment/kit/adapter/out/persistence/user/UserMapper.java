package org.flickit.assessment.kit.adapter.out.persistence.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.user.UserJpaEntity;
import org.flickit.assessment.kit.application.port.in.user.GetUserListUseCase;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static GetUserListUseCase.UserListItem mapJpaEntityToUserItem(UserJpaEntity entity) {
        return new GetUserListUseCase.UserListItem(
            entity.getDisplayName(),
            entity.getEmail()
        );
    }
}
