package org.flickit.flickitassessmentcore.adapter.out.persistence.assessment;

import org.flickit.flickitassessmentcore.application.port.out.assessment.CreateAssessmentPort;

public class AssessmentMapper {
    static AssessmentEntity mapCreateParamToJpaEntity(CreateAssessmentPort.Param param) {
        return new AssessmentEntity(
            null,
            param.code(),
            param.title(),
            param.description(),
            param.creationTime(),
            param.lastModificationDate(),
            param.assessmentKitId(),
            param.colorId(),
            param.spaceId(),
            null);
    }
}
