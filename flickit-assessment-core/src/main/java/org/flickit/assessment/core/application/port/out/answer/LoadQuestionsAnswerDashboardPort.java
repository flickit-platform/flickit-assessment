package org.flickit.assessment.core.application.port.out.answer;

import org.flickit.assessment.core.application.domain.assessmentdashboard.DashboardAnswersQuestions;

import java.util.UUID;

public interface LoadQuestionsAnswerDashboardPort {

    DashboardAnswersQuestions loadQuestionsDashboard(UUID assessmentResultId, long kitVersionId);
}
