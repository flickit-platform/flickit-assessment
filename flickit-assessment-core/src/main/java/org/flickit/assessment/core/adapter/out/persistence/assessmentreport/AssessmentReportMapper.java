package org.flickit.assessment.core.adapter.out.persistence.assessmentreport;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.AssessmentReport;
import org.flickit.assessment.core.application.domain.AssessmentReportMetadata;
import org.flickit.assessment.data.jpa.core.assessmentreport.AssessmentReportJpaEntity;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssessmentReportMapper {

    public static AssessmentReport mapToDomainModel(AssessmentReportJpaEntity entity, AssessmentReportMetadata metadata) {
        return new AssessmentReport(entity.getId(),
            entity.getAssessmentResultId(),
            metadata);
    }

    public static AssessmentReportJpaEntity mapToJpaEntity(UUID assessmentResultId, String metadata) {
        return new AssessmentReportJpaEntity(null, assessmentResultId, metadata);
    }
}
