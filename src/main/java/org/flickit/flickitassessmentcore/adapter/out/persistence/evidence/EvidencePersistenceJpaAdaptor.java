package org.flickit.flickitassessmentcore.adapter.out.persistence.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.domain.Evidence;
import org.flickit.flickitassessmentcore.application.domain.crud.PaginatedResponse;
import org.flickit.flickitassessmentcore.application.port.in.evidence.GetEvidenceListUseCase.EvidenceListItem;
import org.flickit.flickitassessmentcore.application.port.out.evidence.CreateEvidencePort;
import org.flickit.flickitassessmentcore.application.port.out.evidence.LoadEvidencePort;
import org.flickit.flickitassessmentcore.application.port.out.evidence.LoadEvidencesByQuestionAndAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.evidence.SaveEvidencePort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.flickit.flickitassessmentcore.common.ErrorMessageKey;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvidencePersistenceJpaAdaptor implements
    CreateEvidencePort,
    LoadEvidencesByQuestionAndAssessmentPort,
    SaveEvidencePort,
    LoadEvidencePort {

    private final EvidenceJpaRepository repository;

    @Override
    public UUID persist(CreateEvidencePort.Param param) {
        var unsavedEntity = EvidenceMapper.mapCreateParamToJpaEntity(param);
        EvidenceJpaEntity entity = repository.save(unsavedEntity);
        return entity.getId();
    }

    @Override
    public PaginatedResponse<EvidenceListItem> loadEvidencesByQuestionIdAndAssessmentId(Long questionId, UUID assessmentId, int page, int size) {
        var pageResult = repository.findByQuestionIdAndAssessmentIdOrderByLastModificationTimeDesc(questionId, assessmentId, PageRequest.of(page, size));
        var items = pageResult.getContent().stream()
            .map(EvidenceMapper::toEvidenceListItem)
            .toList();
        return new PaginatedResponse<>(
            items,
            pageResult.getNumber(),
            pageResult.getSize(),
            EvidenceJpaEntity.Fields.LAST_MODIFICATION_TIME,
            Sort.Direction.DESC.name().toLowerCase(),
            (int) pageResult.getTotalElements()
        );
    }

    @Override
    public Evidence loadEvidence(UUID id) {
        var evidenceEntity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessageKey.EDIT_EVIDENCE_EVIDENCE_NOT_FOUND));
        return EvidenceMapper.toDomainModel(evidenceEntity);
    }

    @Override
    public UUID saveEvidence(Evidence evidence) {
        var savedEntity = repository.save(EvidenceMapper.toJpaEntity(evidence));
        return savedEntity.getId();
    }
}
