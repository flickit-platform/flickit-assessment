package org.flickit.assessment.core.adapter.out.persistence.assessmentreport;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.common.util.JsonUtils;
import org.flickit.assessment.core.application.domain.AssessmentReport;
import org.flickit.assessment.core.application.domain.AssessmentReportMetadata;
import org.flickit.assessment.core.application.domain.VisibilityType;
import org.flickit.assessment.core.application.port.out.assessmentreport.CreateAssessmentReportPort;
import org.flickit.assessment.data.jpa.core.assessmentreport.AssessmentReportJpaEntity;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssessmentReportMapper {

    public static AssessmentReport mapToDomainModel(AssessmentReportJpaEntity entity) {
        return new AssessmentReport(entity.getId(),
            entity.getAssessmentResultId(),
            JsonUtils.fromJson(entity.getMetadata(), AssessmentReportMetadata.class),
            entity.getPublished(),
            VisibilityType.valueOfById(entity.getVisibility()),
            entity.getLinkHash(),
            entity.getCreationTime(),
            entity.getLastModificationTime(),
            entity.getCreatedBy(),
            entity.getLastModifiedBy());
    }

    public static AssessmentReportJpaEntity mapToJpaEntity(CreateAssessmentReportPort.Param param) {
        return new AssessmentReportJpaEntity(null,
            param.assessmentResultId(),
            JsonUtils.toJson(param.metadata()),
            Boolean.FALSE,
            VisibilityType.RESTRICTED.getId(),
            UUID.randomUUID(),
            param.creationTime(),
            param.creationTime(),
            param.createdBy(),
            param.createdBy());
    }
}
