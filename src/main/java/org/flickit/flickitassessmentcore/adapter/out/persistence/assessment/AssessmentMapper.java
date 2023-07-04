package org.flickit.flickitassessmentcore.adapter.out.persistence.assessment;

import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentListUseCase.AssessmentWithMaturityLevelId;
import org.flickit.flickitassessmentcore.application.port.out.assessment.CreateAssessmentPort;

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

    public static AssessmentWithMaturityLevelId mapToDomainModelWithMaturityLevelId(AssessmentsWithMaturityLevelView assessmentWithMaturityLevel) {
        AssessmentJpaEntity assessmentEntity = assessmentWithMaturityLevel.getAssessment();
        return new AssessmentWithMaturityLevelId(
            assessmentEntity.getId(),
            assessmentEntity.getCode(),
            assessmentEntity.getTitle(),
            assessmentEntity.getCreationTime(),
            assessmentEntity.getLastModificationDate(),
            assessmentEntity.getAssessmentKitId(),
            assessmentEntity.getColorId(),
            assessmentEntity.getSpaceId(),
            assessmentWithMaturityLevel.getMaturityLevelId()
        );
    }
}
