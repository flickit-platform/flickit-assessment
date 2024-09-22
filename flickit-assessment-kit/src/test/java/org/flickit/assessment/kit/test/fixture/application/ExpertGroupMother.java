package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.kit.application.domain.ExpertGroup;

import java.util.UUID;

public class ExpertGroupMother {

    private static long id = 123L;

    public static ExpertGroup createExpertGroup() {
        return new ExpertGroup(id++,
            "title" + id,
            "path/to/picture",
            UUID.randomUUID());
    }

    public static ExpertGroup createExpertGroupWithCreatedBy(UUID createdBy) {
        return new ExpertGroup(id++,
            "title" + id,
            "path/to/picture",
            createdBy);
    }
}
