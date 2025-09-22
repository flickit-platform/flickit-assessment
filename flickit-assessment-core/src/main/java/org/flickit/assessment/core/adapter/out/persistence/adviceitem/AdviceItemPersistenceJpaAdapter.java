package org.flickit.assessment.core.adapter.out.persistence.adviceitem;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AdviceItem;
import org.flickit.assessment.core.application.port.out.adviceitem.*;
import org.flickit.assessment.data.jpa.advice.adviceitem.AdviceItemJpaEntity;
import org.flickit.assessment.data.jpa.advice.adviceitem.AdviceItemJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.ADVICE_ITEM_ID_NOT_FOUND;

@Component("coreAdviceItemPersistenceJpaAdapter")
@RequiredArgsConstructor
public class AdviceItemPersistenceJpaAdapter implements
    CountAdviceItemsPort,
    LoadAdviceItemsPort,
    CreateAdviceItemPort,
    UpdateAdviceItemPort,
    DeleteAdviceItemPort,
    LoadAdviceItemPort,
    LoadAdviceItemListPort {

    private final AdviceItemJpaRepository repository;

    @Override
    public int countByAssessmentResultId(UUID assessmentResultId) {
        return repository.countByAssessmentResultId(assessmentResultId);
    }

    @Override
    public List<AdviceItem> loadAll(UUID assessmentResultId) {
        var sort = Sort.by(
            Sort.Order.desc(AdviceItemJpaEntity.Fields.priority),
            Sort.Order.desc(AdviceItemJpaEntity.Fields.impact),
            Sort.Order.asc(AdviceItemJpaEntity.Fields.cost)
        );

        var pageResult = repository.findByAssessmentResultId(assessmentResultId, Pageable.unpaged(sort));

        return pageResult.stream()
            .map(AdviceItemMapper::mapToDomainModel)
            .toList();
    }

    @Override
    public UUID persist(CreateAdviceItemPort.Param param, UUID assessmentResultId) {
        var entity = AdviceItemMapper.toJpaEntity(param, assessmentResultId);
        return repository.save(entity).getId();
    }

    @Override
    public void persistAll(List<CreateAdviceItemPort.Param> adviceItems, UUID assessmentResultId) {
        var entities = adviceItems.stream()
            .map(e -> AdviceItemMapper.toJpaEntity(e, assessmentResultId))
            .toList();
        repository.saveAll(entities);
    }

    @Override
    public PaginatedResponse<AdviceItem> loadAll(UUID assessmentResultId, int page, int size) {
        Sort sort = Sort.by(
            Sort.Order.desc(AdviceItemJpaEntity.Fields.priority),
            Sort.Order.desc(AdviceItemJpaEntity.Fields.impact),
            Sort.Order.asc(AdviceItemJpaEntity.Fields.cost)
        );

        var pageResult = repository.findByAssessmentResultId(assessmentResultId,
            PageRequest.of(page, size, sort));

        String sortFields = String.join(",",
            AdviceItemJpaEntity.Fields.priority,
            AdviceItemJpaEntity.Fields.impact,
            AdviceItemJpaEntity.Fields.cost
        );

        String sortDirections = String.join(",",
            Sort.Direction.DESC.name().toLowerCase(),
            Sort.Direction.DESC.name().toLowerCase(),
            Sort.Direction.ASC.name().toLowerCase()
        );

        return new PaginatedResponse<>(
            pageResult.stream().map(AdviceItemMapper::mapToDomainModel).toList(),
            pageResult.getNumber(),
            pageResult.getSize(),
            sortFields,
            sortDirections,
            (int) pageResult.getTotalElements());
    }

    @Override
    public void update(UpdateAdviceItemPort.Param param) {
        repository.update(param.id(),
            param.title(),
            param.description(),
            param.cost().getId(),
            param.priority().getId(),
            param.impact().getId(),
            param.lastModificationTime(),
            param.lastModifiedBy());
    }

    @Override
    public Optional<UUID> loadAssessmentIdById(UUID id) {
        return repository.findAssessmentIdById(id);
    }

    @Override
    public boolean existsByAssessmentResultId(UUID assessmentResultId) {
        return repository.existsByAssessmentResultId(assessmentResultId);
    }

    @Override
    public void delete(UUID id) {
        if (!repository.existsById(id))
            throw new ResourceNotFoundException(ADVICE_ITEM_ID_NOT_FOUND);

        repository.deleteById(id);
    }

    @Override
    public void deleteAllAiGenerated(UUID assessmentResultId) {
        repository.deleteByAssessmentResultIdAndCreatedByIsNullAndLastModifiedByIsNull(assessmentResultId);
    }
}
