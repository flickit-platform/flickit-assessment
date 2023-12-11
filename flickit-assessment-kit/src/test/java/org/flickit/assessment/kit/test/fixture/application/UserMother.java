package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.kit.application.port.in.user.GetUserListUseCase;

public class UserMother {

    public static GetUserListUseCase.UserListItem userListItem(String name, String email) {
        return new GetUserListUseCase.UserListItem(name, email);
    }
}
