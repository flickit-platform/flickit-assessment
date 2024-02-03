package org.flickit.assessment.data.jpa.kit.question;

public interface EffectiveQuestionOnAdviceView {
    Long getQuestionId();
    Integer getCurrentOptionIndex();
    Long getAnswerOptionId();
    Integer getAnswerOptionIndex();
    Integer getQuestionImpactWeight();
    double getAnswerOptionImpactValue();
}
