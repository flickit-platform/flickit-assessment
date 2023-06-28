package org.flickit.flickitassessmentcore.application.port.out.evidence;

import org.flickit.flickitassessmentcore.domain.Evidence;

import java.util.UUID;

public interface LoadEvidencePort {

    Result loadEvidence(Param param);

    record Param(UUID id) {}

    record Result(Evidence evidence) {}
}
