package org.flickit.assessment.core.adapter.out.persistence.question;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.question.QuestionJpaEntity;
import org.flickit.assessment.kit.domain.Question;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionMapper {

    public static Question mapToKitDomainModel(QuestionJpaEntity entity) {
        return new Question(
            entity.getCode(),
            entity.getTitle(),
            entity.getDescription(),
            entity.getIndex(),
            entity.getQuestionnaire().getCode(),
            null, // TODO
            null, // TODO
            entity.getMayNotBeApplicable()
        );
    }
}
