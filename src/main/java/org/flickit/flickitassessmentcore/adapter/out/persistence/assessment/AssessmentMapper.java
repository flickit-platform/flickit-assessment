package org.flickit.flickitassessmentcore.adapter.out.persistence.assessment;

import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentListUseCase.AssessmentListItem;
import org.flickit.flickitassessmentcore.application.port.out.assessment.CreateAssessmentPort;
import org.flickit.flickitassessmentcore.application.domain.Assessment;
import org.flickit.flickitassessmentcore.application.domain.AssessmentKit;


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
            param.deletionTime()
        );
    }

    public static Assessment mapToDomainModel(AssessmentJpaEntity entity) {
        AssessmentKit kit = new AssessmentKit(entity.getAssessmentKitId(), null); // TODO
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
            entity.getLastModificationTime()
        );
    }

    public static AssessmentListItem mapToAssessmentListItem(AssessmentListItemView itemView) {
        AssessmentJpaEntity assessmentEntity = itemView.getAssessment();
        return new AssessmentListItem(
            assessmentEntity.getId(),
            assessmentEntity.getTitle(),
            assessmentEntity.getAssessmentKitId(),
            assessmentEntity.getColorId(),
            assessmentEntity.getLastModificationTime(),
            itemView.getMaturityLevelId(),
            itemView.getIsCalculateValid()
        );
    }
}
