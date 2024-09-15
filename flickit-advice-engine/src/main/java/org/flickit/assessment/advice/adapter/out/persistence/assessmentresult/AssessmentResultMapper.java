package org.flickit.assessment.advice.adapter.out.persistence.assessmentresult;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.advice.application.domain.AssessmentResult;
import org.flickit.assessment.advice.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.advice.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssessmentResultMapper {

    public static AssessmentResult mapToDomain(AssessmentResultJpaEntity entity) {
        return new AssessmentResult(entity.getId());
    }

    public static LoadAssessmentResultPort.AssessmentResult mapToDomain(AssessmentResultJpaEntity entity) {
        return new LoadAssessmentResultPort.AssessmentResult(entity.getId(),
            entity.getAssessment().getTitle(),
            entity.getKitVersionId(),
            entity.getLastCalculationTime());
    }
}
