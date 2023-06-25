package org.flickit.flickitassessmentcore.adapter.out.persistence.assessment;

import org.flickit.flickitassessmentcore.application.port.out.assessment.CreateAssessmentPort;
import org.flickit.flickitassessmentcore.domain.Assessment;

public class AssessmentMapper {

    static AssessmentJpaEntity mapCreateParamToJpaEntity(CreateAssessmentPort.Param param) {
        return new AssessmentJpaEntity(
            null,
            param.code(),
            param.title(),
            param.creationTime(),
            param.lastModificationDate(),
            param.assessmentKitId(),
            param.colorId(),
            param.spaceId()
        );
    }

    public static Assessment mapToDomainModel(AssessmentJpaEntity assessmentEntity) {
        return new Assessment(
            assessmentEntity.getId(),
            assessmentEntity.getCode(),
            assessmentEntity.getTitle(),
            assessmentEntity.getCreationTime(),
            assessmentEntity.getLastModificationDate(),
            assessmentEntity.getAssessmentKitId(),
            assessmentEntity.getColorId(),
            assessmentEntity.getSpaceId()
        );
    }
}
