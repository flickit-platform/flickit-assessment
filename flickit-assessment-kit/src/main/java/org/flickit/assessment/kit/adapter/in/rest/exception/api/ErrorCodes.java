package org.flickit.assessment.kit.adapter.in.rest.exception.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorCodes {

    public static final String UNSUPPORTED_DSL_CONTENT_CHANGE = "UNSUPPORTED_DSL_CONTENT_CHANGE";
    public static final String DSL_SYNTAX_ERROR = "DSL_SYNTAX_ERROR";
}
