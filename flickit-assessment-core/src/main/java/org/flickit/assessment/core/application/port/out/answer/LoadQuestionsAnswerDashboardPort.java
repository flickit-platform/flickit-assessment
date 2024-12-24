package org.flickit.assessment.core.application.port.out.answer;

import java.util.List;
import java.util.UUID;

public interface LoadQuestionsAnswerDashboardPort {

    Result loadQuestionsDashboard(UUID assessmentResultId);

    record Result(List<Answer> answers) {

        public record Answer(UUID id, int confidence) {
        }
    }
}
