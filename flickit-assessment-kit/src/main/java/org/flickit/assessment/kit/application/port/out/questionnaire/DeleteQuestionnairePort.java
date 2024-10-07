package org.flickit.assessment.kit.application.port.out.questionnaire;

public interface DeleteQuestionnairePort {

    void delete(long kitVersionId, long questionnaireId);
}
