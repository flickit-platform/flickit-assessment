package org.flickit.assessment.core.adapter.out.persistence.evidence;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.Evidence;
import org.flickit.assessment.core.application.domain.EvidenceType;
import org.flickit.assessment.core.application.port.in.evidence.GetEvidenceListUseCase;
import org.flickit.assessment.core.application.port.in.evidence.GetEvidenceListUseCase.EvidenceListItem;
import org.flickit.assessment.core.application.port.out.evidence.CreateEvidencePort;
import org.flickit.assessment.data.jpa.core.evidence.EvidenceJpaEntity;
import org.flickit.assessment.data.jpa.core.evidence.EvidenceWithAttachmentsCountView;
import org.flickit.assessment.data.jpa.users.user.UserJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EvidenceMapper {

    public static EvidenceJpaEntity mapCreateParamToJpaEntity(CreateEvidencePort.Param param) {
        return new EvidenceJpaEntity(
            null,
            param.description(),
            param.creationTime(),
            param.lastModificationTime(),
            param.createdById(),
            param.createdById(),
            param.assessmentId(),
            param.questionId(),
            param.type(),
            false
        );
    }

    public static EvidenceListItem toEvidenceListItem(EvidenceWithAttachmentsCountView view, UserJpaEntity user) {
        return new EvidenceListItem(
            view.getId(),
            view.getDescription(),
            view.getType() != null ? EvidenceType.values()[view.getType()].getTitle() : null,
            view.getLastModificationTime(),
            view.getAttachmentsCount(),
            new GetEvidenceListUseCase.User(user.getId(), user.getDisplayName(), user.getPicture())
        );
    }

    public static Evidence mapToDomainModel(EvidenceJpaEntity entity) {
        return new Evidence(
            entity.getId(),
            entity.getDescription(),
            entity.getCreatedBy(),
            entity.getLastModifiedBy(),
            entity.getAssessmentId(),
            entity.getQuestionId(),
            entity.getType(),
            entity.getCreationTime(),
            entity.getLastModificationTime(),
            entity.isDeleted()
        );
    }
}
