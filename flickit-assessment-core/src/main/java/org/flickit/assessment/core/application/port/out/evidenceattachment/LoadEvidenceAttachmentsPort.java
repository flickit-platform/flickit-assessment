package org.flickit.assessment.core.application.port.out.evidenceattachment;

import org.flickit.assessment.core.application.domain.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface LoadEvidenceAttachmentsPort {

    List<Result> loadEvidenceAttachments(UUID evidenceId);

    record Result(
        UUID id,
        String filePath,
        String description,
        User createdBy,
        LocalDateTime creationTime
    ){
    }
}
