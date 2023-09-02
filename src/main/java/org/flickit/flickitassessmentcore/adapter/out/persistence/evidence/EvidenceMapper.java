package org.flickit.flickitassessmentcore.adapter.out.persistence.evidence;

import org.flickit.flickitassessmentcore.application.domain.Evidence;
import org.flickit.flickitassessmentcore.application.port.out.evidence.CreateEvidencePort;

public class EvidenceMapper {

    public static EvidenceJpaEntity mapCreateParamToJpaEntity(CreateEvidencePort.Param param) {
        return new EvidenceJpaEntity(
            null,
            param.description(),
            param.creationTime(),
            param.lastModificationTime(),
            param.createdById(),
            param.assessmentId(),
            param.questionId()
        );
    }

    public static Evidence toDomainModel(EvidenceJpaEntity entity) {
        return new Evidence(
            entity.getId(),
            entity.getDescription(),
            entity.getCreatedById(),
            entity.getAssessmentId(),
            entity.getQuestionId(),
            entity.getCreationTime(),
            entity.getLastModificationTime()
        );
    }
}
