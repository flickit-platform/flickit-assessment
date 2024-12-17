package org.flickit.assessment.data.jpa.kit.question;

import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaEntity;

public interface QuestionQuestionnaireView {

    int getQuestionIndex();

    Long getQuestionnaireId();

    String getQuestionnaireTitle();

    QuestionJpaEntity getQuestion();

    QuestionnaireJpaEntity getQuestionnaire();
}
