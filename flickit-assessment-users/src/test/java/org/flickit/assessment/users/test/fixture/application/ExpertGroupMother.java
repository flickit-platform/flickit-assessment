package org.flickit.assessment.users.test.fixture.application;

import org.flickit.assessment.users.application.domain.ExpertGroup;

import java.util.UUID;

public class ExpertGroupMother {

    private static Long id = 134L;

    public static ExpertGroup createExpertGroup(String picturePath, UUID ownerId) {
        return new ExpertGroup(
            id++,
            "Title" + id,
            "Bio" + id,
            "About" + id,
            picturePath,
            "Website" + id,
            ownerId
        );
    }
}
