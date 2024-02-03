package org.flickit.assessment.data.jpa.kit.question;

public interface EffectiveQuestionOnAdviceView {
    Long getEffectiveQuestionId();
    Integer getEffectiveAnsweredOptionIndex();
    Long getEffectiveOptionId();
    Integer getEffectiveOptionIndex();
    Integer getEffectiveQuestionImpactWeight();
    double getEffectiveOptionImpactValue();
}
