package org.flickit.assessment.data.jpa.kit.question;

public interface ImprovableImpactfulQuestionView {
    Long getImpactfulQuestionId();
    Integer getImpactfulAnsweredOptionIndex();
    Long getImpactfulOptionId();
    Integer getImpactfulOptionIndex();
    Integer getImpactfulQuestionImpactWeight();
    double getImpactfulOptionImpactValue();
}
