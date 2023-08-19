package org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult;

import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.CreateAssessmentResultPort;

import java.time.LocalDateTime;

public class AssessmentResultMapper {


    public static AssessmentResultJpaEntity mapToJpaEntity(CreateAssessmentResultPort.Param param) {
        return new AssessmentResultJpaEntity(
            null,
            null,
            null,
            param.isValid(),
            param.lastModificationTime()
        );
    }
}
