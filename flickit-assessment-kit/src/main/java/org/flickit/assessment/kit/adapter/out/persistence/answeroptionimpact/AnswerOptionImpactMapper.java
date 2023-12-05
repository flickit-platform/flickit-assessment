package org.flickit.assessment.kit.adapter.out.persistence.answeroptionimpact;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.asnweroptionimpact.AnswerOptionImpactJpaEntity;
import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaEntity;
import org.flickit.assessment.kit.application.domain.AnswerOptionImpact;
import org.flickit.assessment.kit.application.port.out.answeroptionimpact.CreateAnswerOptionImpactPort;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnswerOptionImpactMapper {

    public static AnswerOptionImpact mapToDomainModel(AnswerOptionImpactJpaEntity entity) {
        return new AnswerOptionImpact(
            entity.getId(),
            entity.getOptionId(),
            entity.getValue()
        );
    }

    public static AnswerOptionImpactJpaEntity mapToJpaEntity(CreateAnswerOptionImpactPort.Param param,
                                                             Optional<QuestionImpactJpaEntity> questionImpactEntity) {
        return new AnswerOptionImpactJpaEntity(
            null,
            questionImpactEntity.orElse(null),
            param.optionId(),
            param.value()
        );
    }
}
