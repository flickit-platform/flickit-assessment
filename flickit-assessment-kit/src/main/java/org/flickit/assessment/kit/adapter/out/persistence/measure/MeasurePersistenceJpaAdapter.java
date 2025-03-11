package org.flickit.assessment.kit.adapter.out.persistence.measure;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.measure.MeasureJpaEntity;
import org.flickit.assessment.data.jpa.kit.measure.MeasureJpaRepository;
import org.flickit.assessment.data.jpa.kit.seq.KitDbSequenceGenerators;
import org.flickit.assessment.kit.application.domain.Measure;
import org.flickit.assessment.kit.application.port.out.measure.CreateMeasurePort;
import org.flickit.assessment.kit.application.port.out.measure.LoadMeasurePort;
import org.flickit.assessment.kit.application.port.out.measure.UpdateMeasurePort;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.flickit.assessment.kit.common.ErrorMessageKey.MEASURE_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class MeasurePersistenceJpaAdapter implements
    CreateMeasurePort,
    LoadMeasurePort,
    UpdateMeasurePort {

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
    public Measure loadByCode(String code) {
        return repository.findByCode(code)
            .map(MeasureMapper::mapToDomainModel)
            .orElseThrow(() -> new ResourceNotFoundException(MEASURE_ID_NOT_FOUND));
    }
}
