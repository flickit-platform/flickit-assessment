package org.flickit.flickitassessmentcore.adapter.in.rest.assessment;

import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentListUseCase.AssessmentListItem;

import java.util.List;

public record GetAssessmentListResponseDto(List<AssessmentListItem> items) {
}
