package org.flickit.flickitassessmentcore.adapter.in.rest.advice.common;

public record ValidationError(String propertyPath,
                              String message) {
}
