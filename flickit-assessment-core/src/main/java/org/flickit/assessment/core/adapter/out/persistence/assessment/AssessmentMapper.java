package org.flickit.assessment.core.adapter.out.persistence.assessment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.Assessment;
import org.flickit.assessment.core.application.domain.AssessmentColor;
import org.flickit.assessment.core.application.domain.AssessmentKit;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentListUseCase.AssessmentListItem;
import org.flickit.assessment.core.application.port.out.assessment.CreateAssessmentPort;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaEntity;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentListItemView;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssessmentMapper {

    static AssessmentJpaEntity mapCreateParamToJpaEntity(CreateAssessmentPort.Param param) {
        return new AssessmentJpaEntity(
            null,
            param.code(),
            param.title(),
            param.assessmentKitId(),
            param.colorId(),
            param.spaceId(),
            param.creationTime(),
            param.lastModificationTime(),
            param.deletionTime(),
            param.deleted(),
            param.createdBy(),
            param.createdBy()
        );
    }

    public static Assessment mapToDomainModel(AssessmentJpaEntity entity) {
        AssessmentKit kit = new AssessmentKit(entity.getAssessmentKitId(), null, null);
        return mapToDomainModel(entity, kit);
    }

    public static Assessment mapToDomainModel(AssessmentJpaEntity entity, AssessmentKit kit) {
        return new Assessment(
            entity.getId(),
            entity.getCode(),
            entity.getTitle(),
            kit,
            entity.getColorId(),
            entity.getSpaceId(),
            entity.getCreationTime(),
            entity.getLastModificationTime(),
            entity.getDeletionTime(),
            entity.isDeleted()
        );
    }

    public static AssessmentListItem mapToAssessmentListItem(AssessmentListItemView itemView) {
        AssessmentJpaEntity assessmentEntity = itemView.getAssessment();
        return new AssessmentListItem(
            assessmentEntity.getId(),
            assessmentEntity.getTitle(),
            assessmentEntity.getAssessmentKitId(),
            assessmentEntity.getSpaceId(),
            AssessmentColor.valueOfById(assessmentEntity.getColorId()),
            assessmentEntity.getLastModificationTime(),
            itemView.getMaturityLevelId(),
            itemView.getIsCalculateValid(),
            itemView.getIsConfidenceValid()
        );
    }

}
