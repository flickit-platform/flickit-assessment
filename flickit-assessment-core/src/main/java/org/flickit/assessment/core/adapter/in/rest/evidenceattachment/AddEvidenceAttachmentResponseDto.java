package org.flickit.assessment.core.adapter.in.rest.evidenceattachment;

import java.util.UUID;

public record AddEvidenceAttachmentResponseDto(UUID attachmentId, String attachmentLink) {
}
