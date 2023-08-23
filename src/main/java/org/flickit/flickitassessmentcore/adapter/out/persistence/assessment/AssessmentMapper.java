package org.flickit.flickitassessmentcore.adapter.out.persistence.assessment;

import org.flickit.flickitassessmentcore.application.port.out.assessment.CreateAssessmentPort;
import org.flickit.flickitassessmentcore.domain.Assessment;
import org.flickit.flickitassessmentcore.domain.AssessmentKit;

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

    public static AssessmentJpaEntity mapToJpaEntity(Assessment assessment) {
        return new AssessmentJpaEntity(
            assessment.getId(),
            assessment.getCode(),
            assessment.getTitle(),
            assessment.getCreationTime(),
            assessment.getLastModificationDate(),
            assessment.getAssessmentKitId(),
            assessment.getColorId(),
            assessment.getSpaceId()
        );
    }
}
