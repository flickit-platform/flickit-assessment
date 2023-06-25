package org.flickit.flickitassessmentcore.application.port.out.evidence;

import org.flickit.flickitassessmentcore.domain.Evidence;

public interface CreateEvidencePort {

    Result createEvidence(Param param);

    record Param(Evidence evidence) {}

    record Result(Evidence evidence) {}
}
