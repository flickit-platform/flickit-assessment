package org.flickit.assessment.common.exception.api;

import java.util.List;

public record ErrorResponsesDto(String code, List<String> messages) {
}
