package org.flickit.assessment.core.adapter.in.rest.subjectinsight;

import org.flickit.assessment.core.application.port.in.subjectinsight.GetSubjectInsightUseCase.AssessorInsight;

public record GetSubjectInsightResponseDto(String defaultInsight,
                                           AssessorInsight assessorInsight,
                                           boolean editable) {
}
