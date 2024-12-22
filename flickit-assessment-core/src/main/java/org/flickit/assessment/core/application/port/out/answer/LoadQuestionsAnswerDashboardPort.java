package org.flickit.assessment.core.application.port.out.answer;

import java.util.List;
import java.util.UUID;

public interface LoadQuestionsAnswerDashboardPort {

    Result loadQuestionsDashboard(long kitVersionId);

    record Result(List<Answer> answers, List<Evidence> evidences, long totalQuestion, long totalEvidences) {

        public record Answer(long id, int confidence) {
        }

        public record Evidence(UUID id, Integer type, Boolean resolved, long questionId) {
        }
    }
}
