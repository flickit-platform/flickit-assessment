package org.flickit.assessment.core.adapter.out.persistence.answer;

import org.flickit.assessment.core.adapter.out.persistence.assessmentresult.AssessmentResultMapper;
import org.flickit.assessment.core.domain.Answer;
import org.flickit.assessment.core.domain.AnswerOption;
import org.flickit.assessment.core.domain.Question;

public class AnswerMapper {

    public static Answer mapToDomainModel(AnswerJpaEntity answerEntity) {
        return new Answer(
          answerEntity.getId(),
            AssessmentResultMapper.mapToDomainModel(answerEntity.getAssessmentResult()),
            new Question(answerEntity.getQuestionId()),
            new AnswerOption(answerEntity.getAnswerOptionId())
        );
    }

    public static AnswerJpaEntity mapToJpaEntity(Answer answer) {
        return new AnswerJpaEntity(
            answer.getId(),
            AssessmentResultMapper.mapToJpaEntity(answer.getAssessmentResult()),
            answer.getQuestion().getId(),
            answer.getAnswerOption().getId()
        );
    }
}
