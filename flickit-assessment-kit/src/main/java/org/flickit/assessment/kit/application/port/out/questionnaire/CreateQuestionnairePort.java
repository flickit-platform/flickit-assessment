package org.flickit.assessment.kit.application.port.out.questionnaire;

import org.flickit.assessment.kit.application.domain.Questionnaire;

import java.util.UUID;

public interface CreateQuestionnairePort {

    Long persist(Questionnaire questionnaire, long kitVersionId, UUID createdBy);
}
