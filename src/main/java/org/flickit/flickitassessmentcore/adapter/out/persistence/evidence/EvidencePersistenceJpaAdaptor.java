package org.flickit.flickitassessmentcore.adapter.out.persistence.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.domain.crud.PaginatedResponse;
import org.flickit.flickitassessmentcore.application.port.in.evidence.GetEvidenceListUseCase.EvidenceListItem;
import org.flickit.flickitassessmentcore.application.port.out.evidence.CreateEvidencePort;
import org.flickit.flickitassessmentcore.application.port.out.evidence.LoadEvidencesByQuestionAndAssessmentPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvidencePersistenceJpaAdaptor implements
    CreateEvidencePort,
    LoadEvidencesByQuestionAndAssessmentPort {
    SaveEvidencePort,
    LoadEvidencePort{

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
            .map(EvidenceMapper::toDomainModel)
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
    public LoadEvidencePort.Result loadEvidence(LoadEvidencePort.Param param) {
        Optional<EvidenceJpaEntity> evidenceEntity = repository.findById(param.id());
        return new LoadEvidencePort.Result(EvidenceMapper.toDomainModel(evidenceEntity.get()));
    }

    @Override
    public SaveEvidencePort.Result saveEvidence(SaveEvidencePort.Param param) {
        return new SaveEvidencePort.Result(repository.save(EvidenceMapper.toJpaEntity(param.evidence())).getId());
    }
}
