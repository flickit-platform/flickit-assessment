package org.flickit.flickitassessmentcore.adapter.in.rest.assessment;

import org.flickit.flickitassessmentcore.application.port.in.assessment.CheckComparativeAssessmentsUseCase.ComparableAssessmentListItem;

import java.util.List;

record CheckComparativeAssessmentsResponseDto(List<ComparableAssessmentListItem> comparableAssessmentListItems) {
}
