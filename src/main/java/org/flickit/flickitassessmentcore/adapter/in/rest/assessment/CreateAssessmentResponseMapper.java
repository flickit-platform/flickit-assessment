package org.flickit.flickitassessmentcore.adapter.in.rest.assessment;

import java.util.UUID;

public class CreateAssessmentResponseMapper {
    public static CreateAssessmentResponseDto mapToResponseDto(UUID id) {
        return new CreateAssessmentResponseDto(id);
    }
}
