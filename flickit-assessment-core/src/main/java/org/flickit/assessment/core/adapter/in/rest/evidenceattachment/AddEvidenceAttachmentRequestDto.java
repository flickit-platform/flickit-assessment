package org.flickit.assessment.core.adapter.in.rest.evidenceattachment;

import org.springframework.web.multipart.MultipartFile;

public record AddEvidenceAttachmentRequestDto(MultipartFile attachment, String description) {
}
