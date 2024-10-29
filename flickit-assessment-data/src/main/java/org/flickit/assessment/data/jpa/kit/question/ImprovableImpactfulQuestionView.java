package org.flickit.assessment.data.jpa.kit.question;

public interface ImprovableImpactfulQuestionView {

    Long getQuestionId();

    Integer getAnsweredOptionIndex();

    Long getOptionId();

    Integer getOptionIndex();

    Integer getQuestionImpactWeight();

    Double getOptionImpactValue();

    double getOptionValue();
}
