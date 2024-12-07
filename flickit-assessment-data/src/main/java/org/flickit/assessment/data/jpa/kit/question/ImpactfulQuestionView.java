package org.flickit.assessment.data.jpa.kit.question;

public interface ImpactfulQuestionView {

    Long getQuestionId();

    Long getOptionId();

    Integer getOptionIndex();

    Integer getQuestionImpactWeight();

    double getOptionValue();
}
