package org.flickit.flickitassessmentcore.adapter.out.persistence.evidence;

import org.flickit.flickitassessmentcore.application.port.out.evidence.CreateEvidencePort;

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
}
