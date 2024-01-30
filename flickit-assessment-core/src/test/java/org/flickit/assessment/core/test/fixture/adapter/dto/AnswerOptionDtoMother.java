package org.flickit.assessment.core.test.fixture.adapter.dto;

import org.flickit.assessment.core.adapter.out.rest.answeroption.AnswerOptionDto;
import org.flickit.assessment.core.adapter.out.rest.questionimpact.QuestionImpactDto;
import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaEntity;

import java.util.List;

public class AnswerOptionDtoMother {

    public static AnswerOptionDto answerOptionDto(Long answerOptionId, Long questionId, List<QuestionImpactJpaEntity> impactEntities) {
        return new AnswerOptionDto(
            answerOptionId,
            questionId,
            impactEntities.stream()
                .map(i -> AnswerOptionImpactDtoMother.answerOptionImpactDto(new QuestionImpactDto(i.getId(), i.getWeight(), i.getMaturityLevel().getId(), i.getAttributeId())))
                .toList()
        );
    }
}
