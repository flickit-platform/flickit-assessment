package org.flickit.assessment.kit.adapter.out.rest.exception;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DslParserRestException extends RuntimeException {

    int httpCode;
}

