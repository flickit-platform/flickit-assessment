package org.flickit.assessment.core.application.domain.assessmentdashboard;

import java.util.List;
import java.util.UUID;

public record Evidences(List<Evidence> evidences, long totalEvidences) {

    public record Evidence(UUID id, Integer type, Boolean resolved, long questionId) {
    }
}
