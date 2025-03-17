package org.flickit.assessment.data.jpa.kit.attribute;

import org.flickit.assessment.data.jpa.core.answer.AnswerJpaEntity;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaEntity;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaEntity;
import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaEntity;

public interface AttributeImpactFullQuestionsView {

    QuestionJpaEntity getQuestion();

    AnswerJpaEntity getAnswer();

    QuestionImpactJpaEntity getQuestionImpact();

    AnswerOptionJpaEntity getAnswerOption();
}
