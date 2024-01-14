package org.flickit.assessment.kit.adapter.in.rest.exception.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorCodes {

    public static final String INVALID_DSL_CONTENT = "INVALID_DSL_CONTENT";
    public static final String UNSUPPORTED_DSL_CONTENT_CHANGE = "UNSUPPORTED_DSL_CONTENT_CHANGE";
    public static final String DSL_HAS_SYNTAX_ERROR = "DSL_HAS_SYNTAX_ERROR";
    public static final String DSL_PARSER_HAS_ERROR = "DSL_PARSER_HAS_ERROR";
}
