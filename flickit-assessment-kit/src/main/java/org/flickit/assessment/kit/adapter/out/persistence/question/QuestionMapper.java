package org.flickit.assessment.kit.adapter.out.persistence.question;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaEntity;
import org.flickit.assessment.kit.application.domain.Question;
import org.flickit.assessment.kit.application.port.out.question.CreateQuestionPort;

import java.time.LocalDateTime;
import java.util.UUID;

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
            entity.getAdvisable(),
            entity.getQuestionnaireId(),
            entity.getCreationTime(),
            entity.getLastModificationTime()
        );
    }

    public static QuestionJpaEntity mapToJpaEntity(CreateQuestionPort.Param param) {
        LocalDateTime creationTime = LocalDateTime.now();
        return new QuestionJpaEntity(
            null,
            UUID.randomUUID(),
            param.code(),
            param.index(),
            param.title(),
            param.hint(),
            param.mayNotBeApplicable(),
            param.advisable(),
            param.kitVersionId(),
            param.questionnaireId(),
            creationTime,
            creationTime,
            param.createdBy(),
            param.createdBy()
        );
    }
}
