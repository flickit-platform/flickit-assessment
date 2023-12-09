package org.flickit.assessment.kit.application.port.out.questionnaire;

import org.flickit.assessment.kit.application.domain.Questionnaire;

import java.util.List;

public interface BatchUpdateQuestionnairePort {

    void batchUpdate(List<Questionnaire> questionnaires, Long kitId);
}
