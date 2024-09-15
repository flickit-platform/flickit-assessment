package org.flickit.assessment.advice.adapter.out.persistence.assessmentresult;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.advice.application.domain.AssessmentResult;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssessmentResultMapper {

    public static AssessmentResult mapToDomain(AssessmentResultJpaEntity entity) {
        return new AssessmentResult(entity.getId());
    }
}
