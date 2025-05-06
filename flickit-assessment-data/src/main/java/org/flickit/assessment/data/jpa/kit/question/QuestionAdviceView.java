package org.flickit.assessment.data.jpa.kit.question;

import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaEntity;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaEntity;

public interface QuestionAdviceView {

    QuestionJpaEntity getQuestion();

    AnswerOptionJpaEntity getOption();

    AttributeJpaEntity getAttribute();

    QuestionnaireJpaEntity getQuestionnaire();
}
