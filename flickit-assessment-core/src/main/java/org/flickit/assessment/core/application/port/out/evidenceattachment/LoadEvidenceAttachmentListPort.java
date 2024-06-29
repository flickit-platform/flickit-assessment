package org.flickit.assessment.core.application.port.out.evidenceattachment;

import java.util.List;
import java.util.UUID;

public interface LoadEvidenceAttachmentListPort {

    List<Result> loadEvidenceAttachmentList(UUID evidenceId);

    record Result(UUID id, UUID evidenceId, String file, String description){
    }
}
