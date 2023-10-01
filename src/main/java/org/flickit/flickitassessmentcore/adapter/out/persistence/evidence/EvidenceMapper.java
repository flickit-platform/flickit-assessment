package org.flickit.flickitassessmentcore.adapter.out.persistence.evidence;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.evidence.GetEvidenceListUseCase.EvidenceListItem;
import org.flickit.flickitassessmentcore.application.port.out.evidence.CreateEvidencePort;

import static org.flickit.flickitassessmentcore.application.service.constant.EvidenceConstants.NOT_DELETED_DELETION_TIME;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EvidenceMapper {

    public static EvidenceJpaEntity mapCreateParamToJpaEntity(CreateEvidencePort.Param param) {
        return new EvidenceJpaEntity(
            null,
            param.description(),
            param.creationTime(),
            param.lastModificationTime(),
            param.createdById(),
            param.assessmentId(),
            param.questionId(),
            NOT_DELETED_DELETION_TIME
        );
    }

    public static EvidenceListItem toEvidenceListItem(EvidenceJpaEntity entity) {
        return new EvidenceListItem(
            entity.getId(),
            entity.getDescription(),
            entity.getCreatedById(),
            entity.getAssessmentId(),
            entity.getLastModificationTime()
        );
    }
}
