package org.flickit.flickitassessmentcore.adapter.out.persistence.evidence;

import org.flickit.flickitassessmentcore.adapter.in.rest.evidence.AddEvidenceRequestDto;
import org.flickit.flickitassessmentcore.application.port.in.evidence.AddEvidenceUseCase;
import org.flickit.flickitassessmentcore.application.port.out.evidence.AddEvidencePort;
import org.flickit.flickitassessmentcore.domain.Assessment;
import org.flickit.flickitassessmentcore.domain.Evidence;

import java.time.LocalDateTime;

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
            entity.getAssessmentId(),
            entity.getQuestionId()
        );
    }

    public static AddEvidencePort.Param toAddEvidencePortParam(AddEvidenceUseCase.Param param) {
        return new AddEvidencePort.Param(
            param.getDescription(),
            param.getCreatedById(),
            param.getAssessmentId(),
            param.getQuestionId()
        );
    }

    public static Evidence toEvidence(AddEvidencePort.Param param) {
        return new Evidence(
            null,
            param.description(),
            LocalDateTime.now(),
            LocalDateTime.now(),
            param.createdById(),
            param.assessmentId(),
            param.questionId()
        );
    }

    public static AddEvidenceUseCase.Param toAddEvidenceUseCaseParam(AddEvidenceRequestDto request) {
        return new AddEvidenceUseCase.Param(
            request.description(),
            request.createdById(),
            request.assessmentId(),
            request.questionId()
        );
    }
}
