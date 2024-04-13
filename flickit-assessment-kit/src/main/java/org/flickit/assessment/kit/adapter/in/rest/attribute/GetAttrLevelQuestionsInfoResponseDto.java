package org.flickit.assessment.kit.adapter.in.rest.attribute;

import org.flickit.assessment.kit.application.port.in.attribute.GetAttrLevelQuestionsInfoUseCase;

import java.util.List;

public record GetAttrLevelQuestionsInfoResponseDto(
    Long id,
    String title,
    int index,
    int questionsCount,
    List<GetAttrLevelQuestionsInfoUseCase.Result.Question> questions
) {
}
