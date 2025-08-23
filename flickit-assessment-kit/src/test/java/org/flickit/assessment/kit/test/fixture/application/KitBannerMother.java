package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.common.application.domain.kit.ImageSize;
import org.flickit.assessment.kit.application.domain.KitBanner;

public class KitBannerMother {

    public static KitBanner createWithKitIdIdAndSize(long kitId, ImageSize size) {
        return new KitBanner(
            kitId,
            size,
            "path/to/file");
    }
}
