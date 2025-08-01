package org.flickit.assessment.data.jpa.core.answer;

public interface QuestionnaireIdQuestionIndexView {

    Long getQuestionnaireId();

    Integer getFirstUnansweredQuestionIndex();
}
