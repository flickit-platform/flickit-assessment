package org.flickit.assessment.kit.application.port.out.questionnaire;

public interface DeleteQuestionnairePort {

    void delete(long questionnaireId, long kitVersionId);
}
