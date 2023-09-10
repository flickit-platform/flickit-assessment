package org.flickit.flickitassessmentcore.application.port.out.assessment;

import org.flickit.flickitassessmentcore.application.port.in.questionnaire.GetQuestionnairesProgressUseCase.Progress;

import java.util.UUID;

public interface GetAssessmentProgressPort {
    Progress<UUID> getAssessmentProgressById(UUID assessmentId);
}
