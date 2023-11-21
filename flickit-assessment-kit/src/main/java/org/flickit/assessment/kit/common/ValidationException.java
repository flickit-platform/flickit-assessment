package org.flickit.assessment.kit.common;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ValidationException extends RuntimeException {

    private final transient Notification validation;
}
