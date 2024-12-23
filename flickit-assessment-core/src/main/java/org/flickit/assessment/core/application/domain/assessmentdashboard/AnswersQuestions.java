package org.flickit.assessment.core.application.domain.assessmentdashboard;

import java.util.List;
import java.util.UUID;

public record AnswersQuestions(List<Answer> answers, long totalQuestion) {

    public record Answer(UUID id, int confidence) {
    }
}
