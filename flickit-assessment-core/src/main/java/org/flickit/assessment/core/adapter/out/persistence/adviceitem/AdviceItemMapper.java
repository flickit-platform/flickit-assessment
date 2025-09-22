package org.flickit.assessment.core.adapter.out.persistence.adviceitem;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.common.application.domain.adviceitem.CostLevel;
import org.flickit.assessment.common.application.domain.adviceitem.ImpactLevel;
import org.flickit.assessment.common.application.domain.adviceitem.PriorityLevel;
import org.flickit.assessment.core.application.domain.AdviceItem;
import org.flickit.assessment.core.application.port.out.adviceitem.CreateAdviceItemPort;
import org.flickit.assessment.data.jpa.advice.adviceitem.AdviceItemJpaEntity;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AdviceItemMapper {

    public static AdviceItem mapToDomainModel(AdviceItemJpaEntity entity) {
        return new AdviceItem(
            entity.getId(),
            entity.getTitle(),
            entity.getDescription(),
            CostLevel.valueOfById(entity.getCost()),
            PriorityLevel.valueOfById(entity.getPriority()),
            ImpactLevel.valueOfById(entity.getImpact()));
    }

    public static AdviceItemJpaEntity toJpaEntity(CreateAdviceItemPort.Param param, UUID assessmentResultId) {
        return new AdviceItemJpaEntity(null,
            param.title(),
            assessmentResultId,
            param.description(),
            param.cost().ordinal(),
            param.priority().ordinal(),
            param.impact().ordinal(),
            param.creationTime(),
            param.creationTime(),
            param.createdBy(),
            param.createdBy());
    }
}
