package org.flickit.assessment.data.jpa.kit.answeroption;

public interface ImpactfulAnswerOptionView {

    int getIndex();

    String getTitle();

    long getQuestionId();

    double getImpactValue();
}
