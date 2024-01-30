package org.flickit.assessment.data.jpa.kit.question.advice;

public interface QuestionAdviceView {

    Long getId();

    String getTitle();

    Integer getIndex();

    OptionAdviceView getOption();

    AttributeAdviceView getAttribute();

    QuestionnaireAdviceView getQuestionnaire();
}
