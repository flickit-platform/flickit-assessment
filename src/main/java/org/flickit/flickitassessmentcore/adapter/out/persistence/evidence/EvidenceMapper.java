package org.flickit.flickitassessmentcore.adapter.out.persistence.evidence;

import org.flickit.flickitassessmentcore.application.port.out.evidence.CreateEvidencePort;
import org.flickit.flickitassessmentcore.domain.Evidence;

public class EvidenceMapper {

    public static EvidenceJpaEntity mapCreateParamToJpaEntity(CreateEvidencePort.Param param) {
        return new EvidenceJpaEntity(
            null,
            param.description(),
            param.creationTime(),
            param.lastModificationDate(),
            param.createdById(),
            param.assessmentId(),
            param.questionId()
        );
    }

    public static EvidenceJpaEntity toJpaEntity(Evidence evidence) {
        return new EvidenceJpaEntity(
            evidence.getId(),
            evidence.getDescription(),
            evidence.getCreationTime(),
            evidence.getLastModificationTime(),
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
            entity.getLastModificationTime(),
            entity.getCreatedById(),
            entity.getAssessmentId(),
            entity.getQuestionId()
        );
    }
}
