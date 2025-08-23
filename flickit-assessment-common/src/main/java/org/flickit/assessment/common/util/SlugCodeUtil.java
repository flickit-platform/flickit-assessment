package org.flickit.assessment.common.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SlugCodeUtil {

    public static String generateSlugCode(String title) {
        return title
            .toLowerCase()
            .strip()
            .replaceAll("\\s+", "-");
    }
}
