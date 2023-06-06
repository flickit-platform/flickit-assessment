package org.flickit.flickitassessmentcore.adapter.out.persistence.answer;

import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultMapper;
import org.flickit.flickitassessmentcore.domain.Answer;
import org.flickit.flickitassessmentcore.domain.AnswerOption;
import org.flickit.flickitassessmentcore.domain.Question;

public class AnswerMapper {

    public static Answer mapToDomainModel(AnswerJpaEntity answerEntity) {
        return new Answer(
          answerEntity.getId(),
            AssessmentResultMapper.mapToDomainModel(answerEntity.getAssessmentResult()),
            new Question(answerEntity.getQuestionId()),
            new AnswerOption(answerEntity.getAnswerOptionId())
        );
    }

    public static AnswerJpaEntity mapToDomainModel(Answer answer) {
        return new AnswerJpaEntity(
            answer.getId(),
            AssessmentResultMapper.mapToJpaEntity(answer.getAssessmentResult()),
            answer.getQuestion().getId(),
            answer.getAnswerOption().getId()
        );
    }
}
