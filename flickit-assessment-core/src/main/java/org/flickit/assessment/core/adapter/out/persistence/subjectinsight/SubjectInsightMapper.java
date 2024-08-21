package org.flickit.assessment.core.adapter.out.persistence.subjectinsight;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.port.out.subjectinsight.CreateSubjectInsightPort;
import org.flickit.assessment.data.jpa.core.subjectinsight.SubjectInsightJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SubjectInsightMapper {

    public static SubjectInsightJpaEntity mapToJpaEntity(CreateSubjectInsightPort.Param param) {
        return new SubjectInsightJpaEntity(param.assessmentResultId(),
            param.subjectId(),
            param.insight(),
            param.insightTime(),
            param.insightBy());
    }
}
