package org.flickit.assessment.core.test.fixture.adapter.dto;

import org.flickit.assessment.core.adapter.out.rest.question.QuestionDto;

import java.util.Arrays;

import static org.flickit.assessment.core.test.fixture.adapter.dto.QuestionImpactDtoMother.createQuestionImpactDto;

public class QuestionDtoMother {

    public static QuestionDto createQuestionDtoWithAffectedLevelAndAttributes(Long questionId, Long maturityLevelId, Long... attributeIds) {
        return new QuestionDto(
            questionId,
            Arrays.stream(attributeIds)
                .map(qavId -> createQuestionImpactDto(maturityLevelId, qavId))
                .toList()
        );
    }
}
