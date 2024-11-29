package org.flickit.assessment.advice.adapter.out.persistence.adviceitem;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.advice.application.domain.adviceitem.AdviceItem;
import org.flickit.assessment.data.jpa.advice.adviceitem.AdviceItemJpaEntity;

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
}
