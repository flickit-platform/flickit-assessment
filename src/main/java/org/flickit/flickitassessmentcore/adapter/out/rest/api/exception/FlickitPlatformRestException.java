package org.flickit.flickitassessmentcore.adapter.out.rest.api.exception;

import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class FlickitPlatformRestException extends RuntimeException {

    int httpCode;
}
