package org.flickit.assessment.core.adapter.out.persistence.kit.question;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.Question;
import org.flickit.assessment.core.application.domain.QuestionImpact;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaEntity;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionMapper {

    public static Question mapToDomainModel(Long id, List<QuestionImpact> impacts) {
        return new Question(
            id,
            null,
            null,
            null,
            null,
            impacts
        );
    }

    public static Question mapToDomainModel(QuestionJpaEntity entity, List<QuestionImpact> impacts) {
        return new Question(
            entity.getId(),
            entity.getTitle(),
            entity.getIndex(),
            entity.getHint(),
            entity.getMayNotBeApplicable(),
            impacts
        );
    }
}
