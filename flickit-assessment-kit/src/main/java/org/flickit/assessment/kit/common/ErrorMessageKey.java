package org.flickit.assessment.kit.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorMessageKey {

    public static final String TRANSLATE_KIT_DSL_UNABLE_TO_PARSE_JSON = "translate-kit-dsl.unable.to.parse.json";

    public static final String FIND_MATURITY_LEVEL_ID_NOT_FOUND = "find-maturity-level.id.notFound";
    public static final String FIND_KIT_ID_NOT_FOUND = "find-kit.id.notFound";

    public static final String UPDATE_KIT_BY_DSL_KIT_ID_NOT_NULL = "update-kit-by-dsl.kitId.notNull";
    public static final String UPDATE_KIT_BY_DSL_DSL_CONTENT_NOT_NULL = "update-kit-by-dsl.dsl-content.notNull";

    public static final String UPDATE_KIT_BY_DSL_ADDITION_UNSUPPORTED = "update-kit-by-dsl.addition.unsupported";
    public static final String UPDATE_KIT_BY_DSL_DELETION_UNSUPPORTED = "update-kit-by-dsl.deletion.unsupported";
    public static final String UPDATE_KIT_BY_DSL_MATURITY_LEVEL_NOT_FOUND = "update-kit-by-dsl.maturity-level.notFound";
    public static final String UPDATE_KIT_BY_DSL_ATTRIBUTE_NOT_FOUND= "update-kit-by-dsl.attribute.notFound";
    public static final String UPDATE_KIT_BY_DSL_ANSWER_OPTION_NOT_FOUND= "update-kit-by-dsl.answer-option.notFound";

    public static String entityNameSingleFirst(String fieldName) {
        return String.format("entities.%s.single.first", fieldName);
    }

    public static String entityNamePlural(String fieldName) {
        return String.format("entities.%s.plural", fieldName);
    }

}
