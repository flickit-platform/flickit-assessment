package org.flickit.assessment.core.adapter.out.persistence.kit.measure;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.Measure;
import org.flickit.assessment.data.jpa.kit.measure.MeasureJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MeasureMapper {

    public static Measure mapToDomainModel(MeasureJpaEntity entity) {
        return new Measure(
            entity.getId(),
            entity.getTitle());
    }
}
