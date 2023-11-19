package org.flickit.assessment.kit.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorMessageKey {

    public static final String UPDATE_KIT_BY_DSL_KIT_ID_NOT_NULL = "update-kit-by-dsl.kitId.notNull";
    public static final String UPDATE_KIT_BY_DSL_DSL_CONTENT_NOT_NULL = "update-kit-by-dsl.dsl-content.notNull";
    public static final String UPDATE_KIT_BY_DSL_DSL_CONTENT_NOT_VALID = "update-kit-by-dsl.dsl-content.notValid";
    public static final String UPDATE_KIT_BY_DSL_MATURITY_LEVEL_NOT_VALID = "update-kit-by-dsl.maturity-level.notValid";
}
