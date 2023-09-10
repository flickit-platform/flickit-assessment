package org.flickit.flickitassessmentcore.adapter.in.rest.questionnaire;

import org.flickit.flickitassessmentcore.application.port.in.questionnaire.GetQuestionnairesProgressUseCase.Progress;

import java.util.List;
import java.util.UUID;

record GetQuestionnairesProgressResponseDto(Progress<UUID> assessmentProgress,
                                            List<Progress<Long>> questionnairesProgress) {
}
