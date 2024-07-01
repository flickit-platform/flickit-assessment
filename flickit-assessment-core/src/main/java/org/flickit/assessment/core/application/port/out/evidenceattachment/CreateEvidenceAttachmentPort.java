package org.flickit.assessment.core.application.port.out.evidenceattachment;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CreateEvidenceAttachmentPort {

    UUID persist(UUID evidenceId, String path, String description, UUID currentUserId, LocalDateTime now);
}
