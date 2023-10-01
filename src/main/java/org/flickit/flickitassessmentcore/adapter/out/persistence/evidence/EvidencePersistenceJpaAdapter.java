package org.flickit.flickitassessmentcore.adapter.out.persistence.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.domain.crud.PaginatedResponse;
import org.flickit.flickitassessmentcore.application.port.in.evidence.GetEvidenceListUseCase.EvidenceListItem;
import org.flickit.flickitassessmentcore.application.port.out.evidence.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.flickitassessmentcore.application.service.constant.EvidenceConstants.NOT_DELETED_DELETION_TIME;

@Component
@RequiredArgsConstructor
public class EvidencePersistenceJpaAdapter implements
    CreateEvidencePort,
    LoadEvidencesByQuestionAndAssessmentPort,
    UpdateEvidencePort,
    DeleteEvidencePort,
    CheckEvidenceExistencePort {

    private final EvidenceJpaRepository repository;

    @Override
    public UUID persist(CreateEvidencePort.Param param) {
        var unsavedEntity = EvidenceMapper.mapCreateParamToJpaEntity(param);
        EvidenceJpaEntity entity = repository.save(unsavedEntity);
        return entity.getId();
    }

    @Override
    public PaginatedResponse<EvidenceListItem> loadEvidencesByQuestionIdAndAssessmentId(Long questionId, UUID assessmentId, int page, int size) {
        var pageResult = repository.findByQuestionIdAndAssessmentIdAndDeletionTimeOrderByLastModificationTimeDesc(
            questionId, assessmentId, NOT_DELETED_DELETION_TIME, PageRequest.of(page, size)
        );
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
    public UpdateEvidencePort.Result update(UpdateEvidencePort.Param param) {
        repository.update(
            param.id(),
            param.description(),
            param.lastModificationTime()
        );
        return new UpdateEvidencePort.Result(param.id());
    }

    @Override
    public void setDeletionTimeById(UUID id, Long deletionTime) {
        repository.setDeletionTimeById(id, deletionTime);
    }

    @Override
    public boolean existsById(UUID id) {
        Optional<EvidenceJpaEntity> entity = repository.findByIdAndDeletionTime(id, NOT_DELETED_DELETION_TIME);
        return entity.isPresent();
    }
}
