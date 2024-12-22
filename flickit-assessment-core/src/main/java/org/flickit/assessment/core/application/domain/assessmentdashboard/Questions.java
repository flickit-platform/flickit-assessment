package org.flickit.assessment.core.application.domain.assessmentdashboard;

import java.util.List;
import java.util.UUID;

public record Questions(List<Answer> answers, List<Evidence> evidences, long totalQuestion, long totalEvidences) {

    public record Answer(long id, int confidence) {
    }

    public record Evidence(UUID id, Integer type, Boolean resolved, long questionId) {
    }
}
