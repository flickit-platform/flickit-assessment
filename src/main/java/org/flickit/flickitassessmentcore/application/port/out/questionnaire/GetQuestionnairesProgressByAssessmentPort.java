package org.flickit.flickitassessmentcore.application.port.out.questionnaire;

import org.flickit.flickitassessmentcore.application.port.in.questionnaire.GetQuestionnairesProgressUseCase;

import java.util.List;
import java.util.UUID;

public interface GetQuestionnairesProgressByAssessmentPort {
    List<GetQuestionnairesProgressUseCase.Progress<Long>> getQuestionnairesProgressByAssessmentId(UUID assessmentId);
}
