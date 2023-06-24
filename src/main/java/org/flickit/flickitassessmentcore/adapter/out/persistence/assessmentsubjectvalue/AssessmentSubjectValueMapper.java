package org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentsubjectvalue;

import org.flickit.flickitassessmentcore.application.port.out.assessmentsubjectvalue.CreateAssessmentSubjectValuePort;

public class AssessmentSubjectValueMapper {

    public static AssessmentSubjectValueJpaEntity mapToJpaEntity(CreateAssessmentSubjectValuePort.Param param){
        return new AssessmentSubjectValueJpaEntity(
            null,
            null,
            param.assessmentSubjectId(),
            null
        );
    }
}
