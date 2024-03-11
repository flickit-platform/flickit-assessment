package org.flickit.assessment.core.adapter.out.persistence.assessmentresult;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.adapter.out.persistence.assessment.AssessmentMapper;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.port.out.assessmentresult.CreateAssessmentResultPort;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;

import java.util.ArrayList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssessmentResultMapper {


    public static AssessmentResultJpaEntity mapToJpaEntity(CreateAssessmentResultPort.Param param) {
        return new AssessmentResultJpaEntity(
            null,
            null,
            param.kitVersionId(),
            null,
            null,
            param.isCalculateValid(),
            param.isConfidenceValid(),
            param.lastModificationTime(),
            param.lastModificationTime(),
            param.lastModificationTime()
        );
    }

    public static AssessmentResult mapToDomainModel(AssessmentResultJpaEntity entity) {
        return new AssessmentResult(
            entity.getId(),
            AssessmentMapper.mapToDomainModel(entity.getAssessment()),
            entity.getKitVersionId(),
            new ArrayList<>(),
            entity.getMaturityLevelId() == null ? null :
                new MaturityLevel(entity.getMaturityLevelId(), 1, 0, new ArrayList<>()),
            entity.getConfidenceValue(),
            entity.getIsCalculateValid(),
            entity.getIsConfidenceValid(),
            entity.getLastModificationTime(),
            entity.getLastCalculationTime(),
            entity.getLastConfidenceCalculationTime()
        );
    }
}
