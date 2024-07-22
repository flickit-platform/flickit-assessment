package org.flickit.assessment.core.application.port.out.evidenceattachment;

import java.util.UUID;

public interface LoadEvidenceAttachmentFilePathPort {

    String loadEvidenceAttachmentFilePath(UUID attachmentId);
}
