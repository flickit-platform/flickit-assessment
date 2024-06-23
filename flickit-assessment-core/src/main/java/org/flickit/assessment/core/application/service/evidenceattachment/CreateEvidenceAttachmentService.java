package org.flickit.assessment.core.application.service.evidenceattachment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.in.evidenceattachment.CreateEvidenceAttachmentUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateEvidenceAttachmentService implements CreateEvidenceAttachmentUseCase {

    @Override
    public String createAttachment(Param param) {
        return "";
    }
}
