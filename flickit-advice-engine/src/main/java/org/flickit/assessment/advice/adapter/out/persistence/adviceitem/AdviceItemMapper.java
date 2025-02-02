package org.flickit.assessment.advice.adapter.out.persistence.adviceitem;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.advice.application.domain.adviceitem.AdviceItem;
import org.flickit.assessment.advice.application.domain.adviceitem.CostLevel;
import org.flickit.assessment.advice.application.domain.adviceitem.ImpactLevel;
import org.flickit.assessment.advice.application.domain.adviceitem.PriorityLevel;
import org.flickit.assessment.data.jpa.advice.adviceitem.AdviceItemJpaEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AdviceItemMapper {

    public static AdviceItemJpaEntity toJpaEntity(AdviceItem adviceItem) {
        return new AdviceItemJpaEntity(null,
            adviceItem.getTitle(),
            adviceItem.getAssessmentResultId(),
            adviceItem.getDescription(),
            adviceItem.getCost().ordinal(),
            adviceItem.getPriority().ordinal(),
            adviceItem.getImpact().ordinal(),
            adviceItem.getCreationTime(),
            adviceItem.getLastModificationTime(),
            adviceItem.getCreatedBy(),
            adviceItem.getLastModifiedBy());
    }

    public static AdviceItem mapToDomainModel(AdviceItemJpaEntity entity) {
        return new AdviceItem(entity.getId(),
            entity.getTitle(),
            entity.getAssessmentResultId(),
            entity.getDescription(),
            CostLevel.valueOfById(entity.getCost()),
            PriorityLevel.valueOfById(entity.getPriority()),
            ImpactLevel.valueOfById(entity.getImpact()),
            entity.getCreationTime(),
            entity.getLastModificationTime(),
            entity.getCreatedBy(),
            entity.getLastModifiedBy());
    }

    public static AdviceItem mapToDomainModel(AdviceItem adviceItem, UUID assessmentResultId) {
        return new AdviceItem(null,
            adviceItem.getTitle(),
            assessmentResultId,
            adviceItem.getDescription(),
            adviceItem.getCost(),
            adviceItem.getPriority(),
            adviceItem.getImpact(),
            LocalDateTime.now(),
            LocalDateTime.now(),
            null,
            null);
    }
}
