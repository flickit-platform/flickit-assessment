package org.flickit.assessment.core.adapter.out.persistence.evidenceattachment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.out.evidenceattachment.LoadEvidenceAttachmentsPort;
import org.flickit.assessment.data.jpa.core.evidenceattachment.EvidenceAttachmentJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvidenceAttachmentPersistenceJpaAdapter implements LoadEvidenceAttachmentsPort {

    private final EvidenceAttachmentJpaRepository repository;

    @Override
    public List<Result> loadEvidenceAttachments(UUID evidenceId) {

        var jpaEntityList =  repository.findByEvidenceId(evidenceId);
        return jpaEntityList.stream().map(EvidenceAttachmentMapper::mapEvidenceAttachment).toList();
    }
}
