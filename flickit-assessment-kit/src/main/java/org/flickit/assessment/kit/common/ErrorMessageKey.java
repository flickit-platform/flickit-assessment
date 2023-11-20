package org.flickit.assessment.kit.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorMessageKey {

    public static final String TRANSLATE_KIT_DSL_UNABLE_TO_PARSE_JSON = "translate-kit-dsl.unable.to.parse.json";

    public static final String UPDATE_KIT_BY_DSL_KIT_ID_NOT_NULL = "update-kit-by-dsl.kitId.notNull";
    public static final String UPDATE_KIT_BY_DSL_DSL_CONTENT_NOT_NULL = "update-kit-by-dsl.dsl-content.notNull";
    public static final String UPDATE_KIT_BY_DSL_KIT_NOT_FOUND = "update-kit-by-dsl.kit.notFound";
    public static final String UPDATE_KIT_BY_DSL_KIT_CHANGE_NOT_VALID = "update-kit-by-dsl.kit-change.notValid";
}
