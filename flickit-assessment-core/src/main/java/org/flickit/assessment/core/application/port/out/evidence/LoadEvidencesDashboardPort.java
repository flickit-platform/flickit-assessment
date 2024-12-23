package org.flickit.assessment.core.application.port.out.evidence;

import java.util.List;
import java.util.UUID;

public interface LoadEvidencesDashboardPort {

    Result loadEvidencesDashboard(UUID assessmentId);

    record Result(List<Evidence> evidences) {

        public record Evidence(UUID id, Integer type, Boolean resolved, long questionId) {
        }
    }
}
