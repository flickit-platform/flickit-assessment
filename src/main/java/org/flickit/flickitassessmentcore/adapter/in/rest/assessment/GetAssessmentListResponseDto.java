package org.flickit.flickitassessmentcore.adapter.in.rest.assessment;

import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentListUseCase.AssessmentWithMaturityLevelId;

import java.util.List;

public record GetAssessmentListResponseDto(List<AssessmentWithMaturityLevelId> items) {
}
