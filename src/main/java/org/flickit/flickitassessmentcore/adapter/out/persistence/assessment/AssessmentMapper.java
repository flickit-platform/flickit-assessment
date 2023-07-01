package org.flickit.flickitassessmentcore.adapter.out.persistence.assessment;

import org.flickit.flickitassessmentcore.application.port.out.assessment.CreateAssessmentPort;
import org.flickit.flickitassessmentcore.domain.Assessment;

public class AssessmentMapper {

    public static AssessmentJpaEntity mapToJpaEntity(Assessment assessment) {
        return new AssessmentJpaEntity(
            assessment.getId(),
            assessment.getCode(),
            assessment.getTitle(),
            assessment.getCreationTime(),
            assessment.getLastModificationDate(),
            assessment.getAssessmentKitId(),
            assessment.getColorId(),
            assessment.getSpaceId());
    }

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
