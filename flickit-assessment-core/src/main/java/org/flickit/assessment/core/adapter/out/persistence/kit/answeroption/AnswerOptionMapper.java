package org.flickit.assessment.core.adapter.out.persistence.kit.answeroption;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.adapter.out.persistence.kit.answeroptionimpact.AnswerOptionImpactMapper;
import org.flickit.assessment.core.application.domain.AnswerOption;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaEntity;
import org.flickit.assessment.data.jpa.kit.asnweroptionimpact.AnswerOptionImpactJpaEntity;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnswerOptionMapper {

    public static AnswerOption mapToDomainModel(AnswerOptionJpaEntity answerOption, List<AnswerOptionImpactJpaEntity> answerOptionImpacts) {
        var impacts = answerOptionImpacts.stream()
            .map(AnswerOptionImpactMapper::mapToDomainModel)
            .toList();
        return new AnswerOption(
            answerOption.getId(),
            answerOption.getIndex(),
            answerOption.getTitle(),
            answerOption.getQuestionId(),
            impacts);
    }
}
