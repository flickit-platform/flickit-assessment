package org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult;

import org.flickit.flickitassessmentcore.adapter.out.persistence.assessment.AssessmentMapper;
import org.flickit.flickitassessmentcore.application.domain.AssessmentResult;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessment.AssessmentMapper;
import org.flickit.flickitassessmentcore.application.domain.AssessmentResult;
import org.flickit.flickitassessmentcore.application.domain.MaturityLevel;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.CreateAssessmentResultPort;

import java.util.ArrayList;

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

    public static AssessmentResult mapToDomainModel(AssessmentResultJpaEntity entity) {
        return new AssessmentResult(
            entity.getId(),
            AssessmentMapper.mapToDomainModel(entity.getAssessment()),
            null,
            null,
            entity.getIsValid(),
            entity.getLastModificationTime()
        );
    }

    public static AssessmentResult mapToDomain(AssessmentResultJpaEntity entity) {
        return new AssessmentResult(
            entity.getId(),
            AssessmentMapper.mapToDomainModel(entity.getAssessment()),
            new ArrayList<>(),
            new MaturityLevel(entity.getMaturityLevelId(), 0, new ArrayList<>()),
            entity.getIsValid(),
            entity.getLastModificationTime()
        );
    }
}
