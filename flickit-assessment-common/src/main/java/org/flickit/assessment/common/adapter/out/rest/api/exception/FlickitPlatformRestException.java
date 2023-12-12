package org.flickit.assessment.common.adapter.out.rest.api.exception;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class FlickitPlatformRestException extends RuntimeException {

    int httpCode;
}
