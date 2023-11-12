package org.flickit.assessment.core.application.port.out.question;

import org.flickit.assessment.kit.domain.Question;

import java.util.List;

public interface LoadAssessmentKitQuestionModelsByQuestionnairePort {

    List<Question> load(long questionnaireId);
}
