package org.flickit.assessment.kit.adapter.out.persistence.measure;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.MeasureTranslation;
import org.flickit.assessment.common.util.JsonUtils;
import org.flickit.assessment.data.jpa.kit.measure.MeasureJpaEntity;
import org.flickit.assessment.kit.application.domain.Measure;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MeasureMapper {

    public static Measure mapToDomainModel(MeasureJpaEntity entity) {
        return new Measure(
            entity.getId(),
            entity.getCode(),
            entity.getTitle(),
            entity.getIndex(),
            entity.getDescription(),
            JsonUtils.fromJsonToMap(entity.getTranslations(), KitLanguage.class, MeasureTranslation.class),
            entity.getCreationTime(),
            entity.getLastModificationTime());
    }

    public static MeasureJpaEntity toJpaEntity(Measure measure, long kitVersionId, UUID createdBy) {
        return new MeasureJpaEntity(
            measure.getId(),
            kitVersionId,
            measure.getTitle(),
            measure.getCode(),
            measure.getIndex(),
            measure.getDescription(),
            JsonUtils.toJson(measure.getTranslations()),
            measure.getCreationTime(),
            measure.getLastModificationTime(),
            createdBy,
            createdBy);
    }
}
