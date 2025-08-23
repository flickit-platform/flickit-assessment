package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.kit.application.domain.AttributeMini;

public class AttributeMiniMother {

    private static long id = 1013;

    public static AttributeMini createAttributeMini() {
        return new AttributeMini(
            ++id,
            "title " + id
        );
    }
}
