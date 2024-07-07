package org.flickit.assessment.core.application.port.out.evidenceattachment;

import org.flickit.assessment.core.application.domain.EvidenceAttachment;

import java.util.UUID;

public interface CreateEvidenceAttachmentPort {

    UUID persist(EvidenceAttachment attachment);
}
