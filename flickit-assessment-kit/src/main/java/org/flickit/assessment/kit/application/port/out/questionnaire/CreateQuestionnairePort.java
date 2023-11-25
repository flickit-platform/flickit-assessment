package org.flickit.assessment.kit.application.port.out.questionnaire;

import org.flickit.assessment.kit.application.domain.Questionnaire;

public interface CreateQuestionnairePort {

    void persist(Questionnaire questionnaire, Long kitId);
}
