package org.flickit.assessment.kit.adapter.out.uploaddsl.exception;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DslParserRestException extends RuntimeException {

    int httpCode;
}

