package org.flickit.assessment.core.application.port.out.evidenceattachment;

import org.springframework.web.multipart.MultipartFile;

public interface UploadEvidenceAttachmentPort {

    String uploadAttachment(MultipartFile attachment);
}
