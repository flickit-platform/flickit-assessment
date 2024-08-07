package org.flickit.assessment.data.jpa.kit.questionnaire;

public interface QuestionnaireListItemView {

    long getId();

    String getTitle();

    String getDescription();

    int getIndex();

    int getQuestionCount();
}
