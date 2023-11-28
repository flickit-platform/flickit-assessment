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
    public static final String UPDATE_KIT_BY_DSL_DSL_QUESTIONNAIRE_DELETION_NOT_ALLOWED = "update-kit-by-dsl.questionnaire-deletion.notAllowed";
    public static final String UPDATE_KIT_BY_DSL_DSL_QUESTIONNAIRE_DELETION_NOT_ALLOWED2 = "update-kit-by-dsl.questionnaire-deletion.notAllowed2";
}
