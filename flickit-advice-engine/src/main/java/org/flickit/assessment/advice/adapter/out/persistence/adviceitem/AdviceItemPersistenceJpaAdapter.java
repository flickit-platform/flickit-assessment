package org.flickit.assessment.advice.adapter.out.persistence.adviceitem;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.domain.adviceitem.AdviceItem;
import org.flickit.assessment.advice.application.port.out.adviceitem.CreateAdviceItemPort;
import org.flickit.assessment.advice.application.port.out.adviceitem.LoadAdviceItemListPort;
import org.flickit.assessment.advice.application.port.out.adviceitem.LoadAdviceItemPort;
import org.flickit.assessment.advice.application.port.out.adviceitem.UpdateAdviceItemPort;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.data.jpa.advice.adviceitem.AdviceItemJpaEntity;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.advice.application.port.out.adviceitem.DeleteAdviceItemPort;
import org.flickit.assessment.data.jpa.advice.adviceitem.AdviceItemJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.advice.common.ErrorMessageKey.ADVICE_ITEM_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class AdviceItemPersistenceJpaAdapter implements
    CreateAdviceItemPort,
    LoadAdviceItemListPort,
    UpdateAdviceItemPort,
    LoadAdviceItemPort,
    DeleteAdviceItemPort {

    private final AdviceItemJpaRepository repository;

    @Override
    public UUID persist(AdviceItem adviceItem) {
        var entity = AdviceItemMapper.toJpaEntity(adviceItem);
        return repository.save(entity).getId();
    }

    @Override
    public PaginatedResponse<AdviceItem> loadAdviceItemList(UUID assessmentResultId, int page, int size) {
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
    public void updateAdviceItem(UpdateAdviceItemPort.Param param) {
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
    public Optional<AdviceItem> loadAdviceItem(UUID id) {
        return repository.findById(id).map(AdviceItemMapper::mapToDomainModel);
    }

    @Override
    public void deleteAdviceItem(UUID id) {
        if (!repository.existsById(id))
            throw new ResourceNotFoundException(ADVICE_ITEM_ID_NOT_FOUND);

        repository.deleteById(id);
    }
}
