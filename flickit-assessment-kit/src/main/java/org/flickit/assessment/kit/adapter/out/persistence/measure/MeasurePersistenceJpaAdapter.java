package org.flickit.assessment.kit.adapter.out.persistence.measure;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.util.JsonUtils;
import org.flickit.assessment.data.jpa.kit.measure.MeasureJpaEntity;
import org.flickit.assessment.data.jpa.kit.measure.MeasureJpaRepository;
import org.flickit.assessment.data.jpa.kit.seq.KitDbSequenceGenerators;
import org.flickit.assessment.kit.application.domain.Measure;
import org.flickit.assessment.kit.application.port.out.measure.CreateMeasurePort;
import org.flickit.assessment.kit.application.port.out.measure.DeleteMeasurePort;
import org.flickit.assessment.kit.application.port.out.measure.LoadMeasurePort;
import org.flickit.assessment.kit.application.port.out.measure.UpdateMeasurePort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.kit.adapter.out.persistence.measure.MeasureMapper.toJpaEntity;
import static org.flickit.assessment.kit.common.ErrorMessageKey.MEASURE_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class MeasurePersistenceJpaAdapter implements
    CreateMeasurePort,
    LoadMeasurePort,
    UpdateMeasurePort,
    DeleteMeasurePort {

    private final MeasureJpaRepository repository;
    private final KitDbSequenceGenerators sequenceGenerators;

    @Override
    public Long persist(Measure measure, long kitVersionId, UUID createdBy) {
        var entity = toJpaEntity(measure, kitVersionId, createdBy);
        entity.setId(sequenceGenerators.generateMeasureId());
        return repository.save(entity).getId();
    }

    @Override
    public void update(Param param) {
        if (!repository.existsByIdAndKitVersionId(param.id(), param.kitVersionId()))
            throw new ResourceNotFoundException(MEASURE_ID_NOT_FOUND);

        repository.update(param.id(),
            param.kitVersionId(),
            param.title(),
            param.code(),
            param.index(),
            param.description(),
            JsonUtils.toJson(param.translations()),
            param.lastModificationTime(),
            param.lastModifiedBy());
    }

    @Override
    public void updateOrders(UpdateOrderParam param) {
        var idToIndex = param.orders().stream()
            .collect(toMap(
                UpdateOrderParam.MeasureOrder::measureId,
                UpdateOrderParam.MeasureOrder::index));

        var entities = repository.findAllByIdInAndKitVersionId(idToIndex.keySet(), param.kitVersionId());
        if (entities.size() != param.orders().size())
            throw new ResourceNotFoundException(MEASURE_ID_NOT_FOUND);

        entities.forEach(x -> {
            var newIndex = idToIndex.get(x.getId());
            x.setIndex(newIndex);
            x.setLastModificationTime(param.lastModificationTime());
            x.setLastModifiedBy(param.lastModifiedBy());
        });
        repository.saveAll(entities);
    }

    @Override
    public PaginatedResponse<LoadMeasurePort.Result> loadAll(long kitVersionId, int page, int size) {
        var pageResult = repository.findAllWithQuestionCountByKitVersionId(kitVersionId, PageRequest.of(page, size));
        var items = pageResult.getContent().stream()
            .map(e -> new LoadMeasurePort.Result(MeasureMapper.mapToDomainModel(e.getMeasure()),
                e.getQuestionCount()))
            .toList();

        return new PaginatedResponse<>(
            items,
            pageResult.getNumber(),
            pageResult.getSize(),
            MeasureJpaEntity.Fields.index,
            Sort.Direction.ASC.name().toLowerCase(),
            (int) pageResult.getTotalElements()
        );
    }

    @Override
    public void delete(long measureId, long kitVersionId) {
        if (!repository.existsByIdAndKitVersionId(measureId, kitVersionId))
            throw new ResourceNotFoundException(MEASURE_ID_NOT_FOUND);

        repository.deleteByIdAndKitVersionId(measureId, kitVersionId);
    }
}
