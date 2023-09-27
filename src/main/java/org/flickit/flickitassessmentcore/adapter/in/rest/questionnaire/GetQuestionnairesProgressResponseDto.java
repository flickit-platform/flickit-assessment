package org.flickit.flickitassessmentcore.adapter.in.rest.questionnaire;

import org.flickit.flickitassessmentcore.application.port.in.questionnaire.GetQuestionnairesProgressUseCase.QuestionnaireProgress;

import java.util.List;

record GetQuestionnairesProgressResponseDto(List<QuestionnaireProgress> items) {
}
