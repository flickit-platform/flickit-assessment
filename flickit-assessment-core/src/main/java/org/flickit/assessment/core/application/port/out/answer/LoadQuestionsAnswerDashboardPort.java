package org.flickit.assessment.core.application.port.out.answer;

import org.flickit.assessment.core.application.domain.assessmentdashboard.Questions;

public interface LoadQuestionsAnswerDashboardPort {

    Questions loadQuestionsDashboard(long kitVersionId);
}
