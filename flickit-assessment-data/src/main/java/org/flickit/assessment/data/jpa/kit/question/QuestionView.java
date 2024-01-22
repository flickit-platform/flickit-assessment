package org.flickit.assessment.data.jpa.kit.question;

public interface QuestionView {
    Long getQuestionId();
    Integer getCurrentOptionIndex();
    Long getAnswerOptionId();
    Integer getAnswerOptionIndex();
    Integer getQuestionImpactWeight();
}
