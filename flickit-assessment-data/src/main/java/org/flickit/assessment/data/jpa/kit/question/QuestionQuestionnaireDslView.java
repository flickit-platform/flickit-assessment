package org.flickit.assessment.data.jpa.kit.question;

public interface QuestionQuestionnaireDslView {

    String getQuestionnaireCode();

    String getAnswerRangeCode();

    QuestionJpaEntity getQuestion();
}
