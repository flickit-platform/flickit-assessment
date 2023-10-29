package org.flickit.flickitassessmentcore.test.fixture.adapter.dto;

import org.flickit.flickitassessmentcore.adapter.out.rest.questionimpact.QuestionImpactDto;

public class QuestionImpactDtoMother {

    private static long questionImpactId = 134L;

    public static QuestionImpactDto createQuestionImpactDto(Long maturityLevelId, Long qualityAttributeId) {
        return new QuestionImpactDto(
            questionImpactId++,
            maturityLevelId.intValue(),
            maturityLevelId,
            qualityAttributeId
        );
    }
}
