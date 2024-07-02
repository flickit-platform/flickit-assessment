package org.flickit.assessment.core.adapter.out.persistence.kit.answeroptionimpact;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.adapter.out.persistence.kit.questionimpact.QuestionImpactMapper;
import org.flickit.assessment.core.application.domain.AnswerOptionImpact;
import org.flickit.assessment.data.jpa.kit.asnweroptionimpact.AnswerOptionImpactJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnswerOptionImpactMapper {

    public static AnswerOptionImpact mapToDomainModel(AnswerOptionImpactJpaEntity entity) {
        return new AnswerOptionImpact(entity.getId(),
            entity.getValue(),
            QuestionImpactMapper.mapToDomainModel(entity.getQuestionImpact()));
    }
}
