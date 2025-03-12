package org.flickit.assessment.kit.adapter.out.persistence.measure;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.measure.MeasureJpaEntity;
import org.flickit.assessment.data.jpa.kit.measure.MeasureJpaRepository;
import org.flickit.assessment.data.jpa.kit.seq.KitDbSequenceGenerators;
import org.flickit.assessment.kit.application.domain.Measure;
import org.flickit.assessment.kit.application.port.out.measure.CreateMeasurePort;
import org.flickit.assessment.kit.application.port.out.measure.DeleteMeasurePort;
import org.flickit.assessment.kit.application.port.out.measure.LoadMeasurePort;
import org.flickit.assessment.kit.application.port.out.measure.UpdateMeasurePort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toMap;
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
        MeasureJpaEntity entity = MeasureMapper.toJpaEntity(measure, kitVersionId, createdBy);
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
    public Measure loadByCode(Long kitVersionId, String code) {
        return repository.findByKitVersionIdAndCode(kitVersionId, code)
            .map(MeasureMapper::mapToDomainModel)
            .orElseThrow(() -> new ResourceNotFoundException(MEASURE_ID_NOT_FOUND));
    }

    @Override
    public List<Measure> loadAll(Long kitVersionId) {
        return repository.findAllByKitVersionIdOrderByIndex(kitVersionId).stream()
            .map(MeasureMapper::mapToDomainModel)
            .toList();
    }

    @Override
    public void delete(long measureId, long kitVersionId) {
        if (!repository.existsByIdAndKitVersionId(measureId, kitVersionId))
            throw new ResourceNotFoundException(MEASURE_ID_NOT_FOUND);

        repository.deleteByIdAndKitVersionId(measureId, kitVersionId);
    }
}
