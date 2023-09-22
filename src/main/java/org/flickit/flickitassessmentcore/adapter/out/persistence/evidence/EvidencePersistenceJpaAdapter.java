package org.flickit.flickitassessmentcore.adapter.out.persistence.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.domain.crud.PaginatedResponse;
import org.flickit.flickitassessmentcore.application.port.in.evidence.GetEvidenceListUseCase.EvidenceListItem;
import org.flickit.flickitassessmentcore.application.port.out.evidence.CreateEvidencePort;
import org.flickit.flickitassessmentcore.application.port.out.evidence.LoadEvidencesByQuestionAndAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.evidence.UpdateEvidencePort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvidencePersistenceJpaAdapter implements
    CreateEvidencePort,
    LoadEvidencesByQuestionAndAssessmentPort,
    UpdateEvidencePort {

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
    public UpdateEvidencePort.Result update(UpdateEvidencePort.Param param) {
        repository.update(
            param.id(),
            param.description(),
            param.lastModificationTime()
        );
        return new UpdateEvidencePort.Result(param.id());
    }
}
