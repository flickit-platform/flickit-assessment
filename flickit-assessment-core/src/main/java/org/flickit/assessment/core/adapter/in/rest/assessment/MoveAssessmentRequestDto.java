package org.flickit.assessment.core.adapter.in.rest.assessment;

public record MoveAssessmentRequestDto(Long fromSpaceId,
                                       Long targetSpaceId) {
}
