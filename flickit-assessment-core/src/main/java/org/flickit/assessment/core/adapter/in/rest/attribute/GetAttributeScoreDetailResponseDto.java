package org.flickit.assessment.core.adapter.in.rest.attribute;

import org.flickit.assessment.core.application.port.in.attribute.GetAttributeScoreDetailUseCase;

import java.util.List;

public record GetAttributeScoreDetailResponseDto(double maxPossibleScore,
                                                 double gainedScore,
                                                 double gainedScorePercentage,
                                                 int questionsCount,
                                                 List<GetAttributeScoreDetailUseCase.Questionnaire> questionnaires) {
}
