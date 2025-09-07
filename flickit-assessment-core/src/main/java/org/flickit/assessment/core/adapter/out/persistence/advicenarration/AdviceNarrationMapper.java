package org.flickit.assessment.core.adapter.out.persistence.advicenarration;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.AdviceNarration;
import org.flickit.assessment.core.application.port.out.advicenarration.CreateAdviceNarrationPort;
import org.flickit.assessment.data.jpa.advice.advicenarration.AdviceNarrationJpaEntity;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AdviceNarrationMapper {

    public static AdviceNarrationJpaEntity toJpaEntity(CreateAdviceNarrationPort.Param param, UUID assessmentResultId) {
        return new AdviceNarrationJpaEntity(null,
            assessmentResultId,
            param.aiNarration(),
            param.assessorNarration(),
            param.approved(),
            param.aiNarrationTime(),
            param.assessorNarrationTime(),
            param.createdBy());
    }

    public static AdviceNarration mapToDomainModel(AdviceNarrationJpaEntity entity) {
        return new AdviceNarration(entity.getId(),
            entity.getAiNarration(),
            entity.getAssessorNarration(),
            entity.isApproved(),
            entity.getAiNarrationTime(),
            entity.getAssessorNarrationTime(),
            entity.getCreatedBy());
    }
}
