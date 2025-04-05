package org.flickit.assessment.core.adapter.out.persistence.kit.measure;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.domain.Measure;
import org.flickit.assessment.core.application.port.out.measure.LoadMeasuresPort;
import org.flickit.assessment.data.jpa.kit.measure.MeasureJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("coreMeasurePersistenceJpaAdapter")
@RequiredArgsConstructor
public class MeasurePersistenceJpaAdapter implements
    LoadMeasuresPort {

    private final MeasureJpaRepository repository;

    @Override
    public List<Measure> loadAll(List<Long> measureIds, long kitVersionId) {
        return repository.findAllByIdInAndKitVersionId(measureIds, kitVersionId).stream()
            .map(MeasureMapper::mapToDomainModel)
            .toList();
    }
}
