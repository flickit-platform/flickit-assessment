package org.flickit.assessment.core.adapter.out.persistence.kit.answeroption;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.AnswerOption;
import org.flickit.assessment.core.application.domain.AnswerOptionImpact;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaEntity;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnswerOptionMapper {

    public static AnswerOption mapToDomainModel(AnswerOptionJpaEntity answerOption,
                                                List<AnswerOptionImpact> answerOptionImpacts) {
        return new AnswerOption(
            answerOption.getId(),
            answerOption.getIndex(),
            answerOption.getTitle(),
            answerOption.getQuestionId(),
            answerOptionImpacts);
    }

    public static AnswerOption mapToDomainModelWithNoImpact(AnswerOptionJpaEntity entity) {
        return new AnswerOption(entity.getId(),
            entity.getIndex(),
            entity.getTitle(),
            entity.getQuestionId(),
            null);
    }
}
