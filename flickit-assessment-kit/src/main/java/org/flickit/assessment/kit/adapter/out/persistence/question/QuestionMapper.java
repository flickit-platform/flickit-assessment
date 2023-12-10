package org.flickit.assessment.kit.adapter.out.persistence.question;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaEntity;
import org.flickit.assessment.kit.application.domain.Question;
import org.flickit.assessment.kit.application.port.out.question.CreateQuestionPort;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionMapper {
    public static Question mapToDomainModel(QuestionJpaEntity entity) {
        return new Question(
            entity.getId(),
            entity.getCode(),
            entity.getTitle(),
            entity.getIndex(),
            entity.getDescription(),
            entity.getMayNotBeApplicable(),
            entity.getQuestionnaireId(),
            entity.getCreationTime(),
            entity.getLastModificationTime()
        );
    }

    public static QuestionJpaEntity mapToJpaEntity(CreateQuestionPort.Param param) {
        return new QuestionJpaEntity(
            null,
            param.code(),
            param.title(),
            param.description(),
            LocalDateTime.now(),
            LocalDateTime.now(),
            param.index(),
            param.questionnaireId(),
            param.mayNotBeApplicable()
        );
    }
}
