package org.flickit.assessment.kit.util;

import lombok.experimental.UtilityClass;

import static org.flickit.assessment.common.util.SlugCodeUtil.generateSlugCode;

@UtilityClass
public class GenerateCodeUtil {

    private static final String PREFIX = "C";

    public String generateCode(String title) {
        return PREFIX + generateSlugCode(title).hashCode();
    }
}
