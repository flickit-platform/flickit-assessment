package org.flickit.assessment.kit.application.port.out.questionnaire;

import org.flickit.assessment.kit.application.domain.Questionnaire;

public interface LoadQuestionnairePort {

    Questionnaire load(Long id, Long kitVersionId);
}
