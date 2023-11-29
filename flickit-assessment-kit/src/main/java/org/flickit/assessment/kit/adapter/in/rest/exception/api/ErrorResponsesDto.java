package org.flickit.assessment.kit.adapter.in.rest.exception.api;

import java.util.List;

public record ErrorResponsesDto(String code, List<String> messages) {
}
