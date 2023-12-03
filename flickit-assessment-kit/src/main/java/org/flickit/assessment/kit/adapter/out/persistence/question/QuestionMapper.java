package org.flickit.assessment.kit.adapter.out.persistence.question;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaEntity;
import org.flickit.assessment.kit.application.domain.Question;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionMapper {
    public static Question mapToDomainModel(QuestionJpaEntity entity) {
        return new Question(
            entity.getId(),
            entity.getCode(),
            entity.getTitle(),
            entity.getIndex(),
            entity.getHint(),
            entity.getMayNotBeApplicable(),
            entity.getQuestionnaireId(),
            entity.getCreationTime(),
            entity.getLastModificationTime()
        );
    }
}
