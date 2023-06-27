package org.flickit.flickitassessmentcore.adapter.in.rest.assessment;

import org.flickit.flickitassessmentcore.domain.Assessment;

import java.util.List;

public record GetAssessmentListResponseDto(List<Assessment> assessments) {
}
