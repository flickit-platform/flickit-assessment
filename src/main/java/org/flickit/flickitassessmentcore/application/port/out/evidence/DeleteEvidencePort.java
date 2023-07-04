package org.flickit.flickitassessmentcore.application.port.out.evidence;

import java.util.UUID;

public interface DeleteEvidencePort {

    void deleteEvidence(Param param);

    record Param(UUID id) {}
}
