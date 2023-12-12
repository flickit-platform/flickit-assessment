package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitUserListUseCase;

public class UserMother {

    public static GetKitUserListUseCase.KitUserListItem userListItem(String name, String email) {
        return new GetKitUserListUseCase.KitUserListItem(name, email);
    }
}
