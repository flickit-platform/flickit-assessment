package org.flickit.flickitassessmentcore.adapter.out.persistence.evidence;

import org.flickit.flickitassessmentcore.domain.Assessment;
import org.flickit.flickitassessmentcore.domain.Evidence;

public class EvidenceMapper {

    public static EvidenceJpaEntity toJpaEntity(Evidence evidence) {
        return new EvidenceJpaEntity(
            evidence.getId(),
            evidence.getDescription(),
            evidence.getCreationTime(),
            evidence.getLastModificationDate(),
            evidence.getCreatedById(),
            evidence.getAssessmentId(),
            evidence.getQuestionId()
        );
    }

    public static Evidence toDomainModel(EvidenceJpaEntity entity) {
        return new Evidence(
            entity.getId(),
            entity.getDescription(),
            entity.getCreationTime(),
            entity.getLastModificationDate(),
            entity.getCreatedById(),
            entity.getAssessmentId(), // TODO: How to get the whole model?
            entity.getQuestionId()
        );
    }
}
