package org.flickit.assessment.core.application.port.out.questionnaire;

import org.flickit.assessment.core.application.port.in.questionnaire.GetQuestionnairesProgressUseCase.QuestionnaireProgress;

import java.util.List;
import java.util.UUID;

public interface GetQuestionnairesProgressPort {

    List<QuestionnaireProgress> getQuestionnairesProgressByAssessmentId(UUID assessmentId);
}

