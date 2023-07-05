package org.flickit.flickitassessmentcore.adapter.out.persistence.subjectvalue;

import org.flickit.flickitassessmentcore.application.port.out.subjectvalue.CreateSubjectValuePort;

public class SubjectValueMapper {

    public static SubjectValueJpaEntity mapToJpaEntity(CreateSubjectValuePort.Param param){
        return new SubjectValueJpaEntity(
            null,
            null,
            param.subjectId(),
            null
        );
    }
}
