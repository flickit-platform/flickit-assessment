package org.flickit.assessment.kit.application.port.out.questionnaire;

import org.flickit.assessment.kit.application.domain.Questionnaire;

import java.util.List;

public interface LoadQuestionnairePort {

    List<Questionnaire> loadByKitVersionId(Long kitVersionId);
}
