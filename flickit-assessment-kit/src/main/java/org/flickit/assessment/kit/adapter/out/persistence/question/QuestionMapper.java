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
            entity.getHint(),
            entity.getMayNotBeApplicable(),
            entity.getQuestionnaireId(),
            entity.getCreationTime(),
            entity.getLastModificationTime()
        );
    }

    public static QuestionJpaEntity mapToJpaEntity(CreateQuestionPort.Param param, Long lastReferenceNumber) {
        return new QuestionJpaEntity(
            null,
            param.code(),
            param.index(),
            param.title(),
            param.hint(),
            param.mayNotBeApplicable(),
            param.questionnaireId(),
            LocalDateTime.now(),
            LocalDateTime.now(),
            param.createdBy(),
            param.createdBy(),
            lastReferenceNumber + 1,
            param.kitId()
        );
    }
}
