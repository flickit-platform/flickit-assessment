package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.kit.application.domain.KitBanner;

public class KitBannerMother {

    private static Long kitId = 123L;

    public static KitBanner create() {
        return new KitBanner(
            kitId++,
            "path/to/file");
    }
}
