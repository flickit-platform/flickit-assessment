package org.flickit.flickitassessmentcore.adapter.out.persistence.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.out.evidence.DeleteEvidencePort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.DELETE_EVIDENCE_EVIDENCE_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class EvidencePersistenceJpaAdaptor implements DeleteEvidencePort {

    private final EvidenceJpaRepository repository;

    @Override
    public void deleteEvidence(Param param) {
        Optional<EvidenceJpaEntity> evidenceJpa = repository.findById(param.id());
        if (evidenceJpa.isPresent()) {
            repository.delete(evidenceJpa.get());
        } else {
            throw new ResourceNotFoundException(DELETE_EVIDENCE_EVIDENCE_NOT_FOUND);
        }
    }
}
