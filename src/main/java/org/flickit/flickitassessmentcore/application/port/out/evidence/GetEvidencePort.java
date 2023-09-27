package org.flickit.flickitassessmentcore.application.port.out.evidence;

import org.flickit.flickitassessmentcore.application.domain.Evidence;

import java.util.Optional;
import java.util.UUID;

public interface GetEvidencePort {

    Optional<Evidence> getEvidenceById(UUID id);
}
