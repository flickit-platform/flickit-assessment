package org.flickit.flickitassessmentcore.adapter.out.persistence.evidence;

import org.flickit.flickitassessmentcore.application.port.in.evidence.AddEvidenceUseCase;
import org.flickit.flickitassessmentcore.application.port.out.evidence.CreateEvidencePort;

import java.time.LocalDateTime;

public class EvidenceMapper {

    public static EvidenceJpaEntity toJpaEntity(CreateEvidencePort.Param param) {
        return new EvidenceJpaEntity(
            null,
            param.description(),
            LocalDateTime.now(),
            LocalDateTime.now(),
            param.createdById(),
            param.assessmentId(),
            param.questionId()
        );
    }
}
