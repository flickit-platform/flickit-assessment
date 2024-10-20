package org.flickit.assessment.core.application.port.out.evidence;

import org.flickit.assessment.core.application.domain.Evidence;

import java.util.UUID;

public interface LoadEvidencePort {

    Evidence loadNotDeletedEvidence(UUID id);
}
