package org.flickit.assessment.kit.adapter.in.rest.attribute;

import org.flickit.assessment.kit.application.port.in.attribute.GetKitAttributeLevelQuestionsDetailUseCase;

import java.util.List;

public record GetKitAttributeLevelQuestionsDetailResponseDto(
    Long id,
    String title,
    int index,
    int questionsCount,
    List<GetKitAttributeLevelQuestionsDetailUseCase.Result.Question> questions
) {
}
