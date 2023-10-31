package org.flickit.assessment.core.application.port.out.evidence;

import java.util.UUID;

public interface DeleteEvidencePort {

    void deleteById(UUID id);

}
