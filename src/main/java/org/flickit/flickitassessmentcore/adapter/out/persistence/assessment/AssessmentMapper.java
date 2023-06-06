package org.flickit.flickitassessmentcore.adapter.out.persistence.assessment;

import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentcolor.AssessmentColorMapper;
import org.flickit.flickitassessmentcore.domain.Assessment;
import org.flickit.flickitassessmentcore.domain.AssessmentKit;
import org.flickit.flickitassessmentcore.domain.MaturityLevel;

public class AssessmentMapper {

    public static Assessment mapToDomainModel(AssessmentJpaEntity assessmentEntity) {
        return new Assessment(assessmentEntity.getId(),
            assessmentEntity.getCode(),
            assessmentEntity.getTitle(),
            assessmentEntity.getDescription(),
            assessmentEntity.getCreationTime(),
            assessmentEntity.getLastModificationDate(),
            new AssessmentKit(assessmentEntity.getAssessmentKitId()),
            AssessmentColorMapper.mapToDomainModel(assessmentEntity.getColor()),
            assessmentEntity.getSpaceId(),
            new MaturityLevel(assessmentEntity.getMaturityLevelId()));
    }

    public static AssessmentJpaEntity mapToJpaEntity(Assessment assessment) {
        return new AssessmentJpaEntity(assessment.getId(),
            assessment.getCode(),
            assessment.getTitle(),
            assessment.getDescription(),
            assessment.getCreationTime(),
            assessment.getLastModificationDate(),
            assessment.getAssessmentKit().getId(),
            AssessmentColorMapper.mapToJpaEntity(assessment.getColor()),
            assessment.getSpaceId(),
            assessment.getMaturityLevel().getId());
    }
}
