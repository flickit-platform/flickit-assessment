package org.flickit.assessment.kit.adapter.out.persistence.question;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaEntity;
import org.flickit.assessment.data.jpa.kit.question.QuestionQuestionnaireView;
import org.flickit.assessment.kit.application.domain.Question;
import org.flickit.assessment.kit.application.domain.dsl.QuestionDslModel;
import org.flickit.assessment.kit.application.port.out.question.CreateQuestionPort;
import org.flickit.assessment.kit.application.port.out.question.LoadQuestionsPort;

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
            entity.getAdvisable(),
            entity.getAnswerRangeId(),
            entity.getQuestionnaireId(),
            entity.getCreationTime(),
            entity.getLastModificationTime()
        );
    }

    public static LoadQuestionsPort.Result mapToPortResult(QuestionQuestionnaireView view) {
        return new LoadQuestionsPort.Result(view.getQuestionIndex(), view.getQuestionnaireId(), view.getQuestionnaireTitle());
    }

    public static QuestionJpaEntity mapToJpaEntity(CreateQuestionPort.Param param) {
        LocalDateTime creationTime = LocalDateTime.now();
        return new QuestionJpaEntity(
            null,
            param.kitVersionId(),
            param.code(),
            param.index(),
            param.title(),
            param.hint(),
            param.mayNotBeApplicable(),
            param.advisable(),
            param.questionnaireId(),
            param.answerRangeId(),
            creationTime,
            creationTime,
            param.createdBy(),
            param.createdBy()
        );
    }

    public static QuestionDslModel mapToDslModel(Question question, String questionnaireCode, String answerRangeCode) {
        return QuestionDslModel.builder()
            .code(question.getCode())
            .index(question.getIndex())
            .title(question.getTitle())
            .description(question.getHint())
            .questionnaireCode(questionnaireCode)
            .answerRangeCode(answerRangeCode)
            .mayNotBeApplicable(question.getMayNotBeApplicable())
            .advisable(question.getAdvisable())
            .build();
    }
}
