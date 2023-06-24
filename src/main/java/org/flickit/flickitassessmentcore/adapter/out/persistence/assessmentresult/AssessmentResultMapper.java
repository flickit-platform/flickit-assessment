package org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult;

import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.CreateAssessmentResultPort;

public class AssessmentResultMapper {

    public static AssessmentResultJpaEntity mapToJpaEntity(CreateAssessmentResultPort.Param param) {
        return new AssessmentResultJpaEntity(
            null,
            null,
            null,
            null,
            param.isValid()
        );
    }
}
