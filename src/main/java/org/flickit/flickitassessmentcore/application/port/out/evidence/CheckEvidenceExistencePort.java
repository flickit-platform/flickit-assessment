package org.flickit.flickitassessmentcore.application.port.out.evidence;

import java.util.UUID;

public interface CheckEvidenceExistencePort {

    boolean existsById(UUID id);
}
