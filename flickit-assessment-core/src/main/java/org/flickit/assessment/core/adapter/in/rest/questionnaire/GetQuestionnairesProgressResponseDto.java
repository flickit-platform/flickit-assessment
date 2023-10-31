package org.flickit.assessment.core.adapter.in.rest.questionnaire;

import org.flickit.assessment.core.application.port.in.questionnaire.GetQuestionnairesProgressUseCase.QuestionnaireProgress;

import java.util.List;

record GetQuestionnairesProgressResponseDto(List<QuestionnaireProgress> items) {
}
