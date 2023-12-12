package org.flickit.assessment.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.api.Notification;

@Getter
@RequiredArgsConstructor
public class ValidationException extends RuntimeException {

    private final transient String code;
    private final transient Notification validation;
}
