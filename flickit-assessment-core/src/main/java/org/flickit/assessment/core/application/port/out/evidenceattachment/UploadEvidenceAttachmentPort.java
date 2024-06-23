package org.flickit.assessment.core.application.port.out.evidenceattachment;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface UploadEvidenceAttachmentPort {
    String uploadAttachment(MultipartFile attachment, UUID fileName);
}
