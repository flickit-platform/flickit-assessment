package org.flickit.flickitassessmentcore.adapter.in.rest.exception.common;

public record ValidationError(String propertyPath,
                              String message) {
}
