package org.flickit.assessment.core.application.port.out.questionnaire;

import org.flickit.assessment.kit.domain.Questionnaire;

import java.util.List;

public interface LoadAssessmentKitQuestionnaireModelsByKitPort {

    List<Questionnaire> load(Long kitId);
}
