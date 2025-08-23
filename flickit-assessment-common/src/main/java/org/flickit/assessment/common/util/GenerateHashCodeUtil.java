package org.flickit.assessment.common.util;

import lombok.experimental.UtilityClass;

import static org.flickit.assessment.common.util.SlugCodeUtil.generateSlugCode;

@UtilityClass
public class GenerateHashCodeUtil {

    private static final String PREFIX = "C";

    public static String generateCode(String title) {
        return PREFIX + generateSlugCode(title).hashCode();
    }
}
