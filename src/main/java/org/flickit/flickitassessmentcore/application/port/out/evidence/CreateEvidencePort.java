package org.flickit.flickitassessmentcore.application.port.out.evidence;

import org.flickit.flickitassessmentcore.domain.Evidence;

import java.util.UUID;

public interface CreateEvidencePort {

    public Result createEvidence(Param param);

    record Param(Evidence evidence) {}

    record Result(Evidence evidence) {}
}
