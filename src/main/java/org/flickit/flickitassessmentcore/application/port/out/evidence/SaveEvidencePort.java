package org.flickit.flickitassessmentcore.application.port.out.evidence;

import org.flickit.flickitassessmentcore.application.domain.Evidence;

import java.util.UUID;

public interface SaveEvidencePort {

    UUID saveEvidence(Evidence evidence);

}
