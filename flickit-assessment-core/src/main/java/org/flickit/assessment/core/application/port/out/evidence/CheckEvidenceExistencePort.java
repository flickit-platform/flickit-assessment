package org.flickit.assessment.core.application.port.out.evidence;

import java.util.UUID;

public interface CheckEvidenceExistencePort {

    boolean existsById(UUID id);
}
