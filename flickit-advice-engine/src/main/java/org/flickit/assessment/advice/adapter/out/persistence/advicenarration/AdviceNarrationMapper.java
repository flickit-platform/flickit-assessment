package org.flickit.assessment.advice.adapter.out.persistence.advicenarration;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.advice.application.domain.AdviceNarration;
import org.flickit.assessment.data.jpa.advice.advicenarration.AdviceNarrationJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AdviceNarrationMapper {

    public static AdviceNarration mapToDomainModel(AdviceNarrationJpaEntity entity) {
        return new AdviceNarration(entity.getId(),
            entity.getAssessmentResultId(),
            entity.getAiNarration(),
            entity.getAssessorNarration(),
            entity.getAiNarrationTime(),
            entity.getAssessorNarrationTime(),
            entity.getCreatedBy());
    }
}
