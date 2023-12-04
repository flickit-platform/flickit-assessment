package org.flickit.assessment.kit.application.port.out.questionnaire;

import org.flickit.assessment.kit.application.domain.Questionnaire;

public interface LoadQuestionnaireByCodePort {

    Questionnaire loadByCode(String code, Long kitId);
}
