package org.flickit.assessment.advice.adapter.out.persistence.advicenarration;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.advice.application.domain.AdviceNarration;
import org.flickit.assessment.common.application.domain.ID;
import org.flickit.assessment.data.jpa.advice.advicenarration.AdviceNarrationJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AdviceNarrationMapper {

    public static AdviceNarrationJpaEntity toJpaEntity(AdviceNarration adviceNarration) {
        return new AdviceNarrationJpaEntity(null,
            ID.fromDomain(adviceNarration.getAssessmentResultId()),
            adviceNarration.getAiNarration(),
            adviceNarration.getAssessorNarration(),
            adviceNarration.getAiNarrationTime(),
            adviceNarration.getAssessorNarrationTime(),
            ID.fromDomain(adviceNarration.getCreatedBy()));
    }

    public static AdviceNarration toDomain(AdviceNarrationJpaEntity entity) {
        return new AdviceNarration(ID.toDomain(entity.getId()),
            ID.toDomain(entity.getAssessmentResultId()),
            entity.getAiNarration(),
            entity.getAssessorNarration(),
            entity.getAiNarrationTime(),
            entity.getAssessorNarrationTime(),
            ID.toDomain(entity.getCreatedBy()));
    }
}
