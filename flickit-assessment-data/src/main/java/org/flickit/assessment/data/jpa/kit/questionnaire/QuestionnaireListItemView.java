package org.flickit.assessment.data.jpa.kit.questionnaire;

import java.time.LocalDateTime;

public interface QuestionnaireListItemView {

    long getId();

    String getCode();

    String getTitle();

    String getDescription();

    int getIndex();

    LocalDateTime getCreationTime();

    LocalDateTime getLastModificationTime();

    int getQuestionCount();
}
