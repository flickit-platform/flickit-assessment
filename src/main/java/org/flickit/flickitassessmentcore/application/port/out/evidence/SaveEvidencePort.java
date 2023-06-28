package org.flickit.flickitassessmentcore.application.port.out.evidence;

import org.flickit.flickitassessmentcore.domain.Evidence;

import java.util.UUID;

public interface SaveEvidencePort {

    Result saveEvidence(Param param);

    record Param(Evidence evidence) {}

    record Result(UUID id) {}
}
