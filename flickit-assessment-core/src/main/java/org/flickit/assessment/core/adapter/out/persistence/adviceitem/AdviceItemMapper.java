package org.flickit.assessment.core.adapter.out.persistence.adviceitem;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.common.application.domain.adviceitem.CostLevel;
import org.flickit.assessment.common.application.domain.adviceitem.ImpactLevel;
import org.flickit.assessment.common.application.domain.adviceitem.PriorityLevel;
import org.flickit.assessment.core.application.domain.AdviceItem;
import org.flickit.assessment.data.jpa.advice.adviceitem.AdviceItemJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AdviceItemMapper {

    public static AdviceItem mapToDomainModel(AdviceItemJpaEntity entity) {
        return new AdviceItem(
            entity.getId(),
            entity.getTitle(),
            entity.getDescription(),
            CostLevel.valueOfById(entity.getCost()) != null ? CostLevel.valueOfById(entity.getCost()).getTitle() : null,
            PriorityLevel.valueOfById(entity.getPriority()).getTitle(),
            ImpactLevel.valueOfById(entity.getImpact()).getTitle());
    }
}
