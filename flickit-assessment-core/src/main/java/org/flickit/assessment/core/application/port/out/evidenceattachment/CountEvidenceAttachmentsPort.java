package org.flickit.assessment.core.application.port.out.evidenceattachment;

import java.util.UUID;

public interface CountEvidenceAttachmentsPort {

    int countAttachments(UUID evidenceId);
}
