package org.flickit.assessment.data.jpa.kit.subject;

public interface SubjectWithQuestionnaireIdView {

    Long getId();

    String getTitle();

    String getTranslations();

    Long getQuestionnaireId();
}
