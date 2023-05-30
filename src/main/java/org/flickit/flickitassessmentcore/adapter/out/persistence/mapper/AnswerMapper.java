package org.flickit.flickitassessmentcore.adapter.out.persistence.mapper;

import org.flickit.flickitassessmentcore.adapter.out.persistence.entity.AnswerEntity;
import org.flickit.flickitassessmentcore.domain.Answer;
import org.flickit.flickitassessmentcore.domain.AnswerOption;
import org.flickit.flickitassessmentcore.domain.Question;

public class AnswerMapper {

    private final AssessmentResultMapper assessmentResultMapper = new AssessmentResultMapper();

    public Answer mapToDomainModel(AnswerEntity answerEntity) {
        return new Answer(
          answerEntity.getId(),
            assessmentResultMapper.mapToDomainModel(answerEntity.getAssessmentResult()),
            new Question(answerEntity.getQuestionId()),
            new AnswerOption(answerEntity.getAnswerOptionId())
        );
    }

    public AnswerEntity mapToDomainModel(Answer answer) {
        return new AnswerEntity(
            answer.getId(),
            assessmentResultMapper.mapToJpaEntity(answer.getAssessmentResult()),
            answer.getQuestion().getId(),
            answer.getAnswerOption().getId()
        );
    }
}
