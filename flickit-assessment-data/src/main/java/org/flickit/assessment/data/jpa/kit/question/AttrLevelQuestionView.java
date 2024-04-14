package org.flickit.assessment.data.jpa.kit.question;

public interface AttrLevelQuestionView {

    long getId();

    int getIndex();

    String getTitle();

    boolean getMayNotBeApplicable();

    int getWeight();

    long getQuestionnaireId();
}
