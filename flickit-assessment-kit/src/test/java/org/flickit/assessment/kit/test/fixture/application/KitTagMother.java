package org.flickit.assessment.kit.test.fixture.application;

import org.flickit.assessment.kit.application.domain.KitTag;

public class KitTagMother {

    private static long id = 1589L;

    public static KitTag createKitTag(String title) {
        return new KitTag(id++, "c-" + title, title + id);
    }
}
