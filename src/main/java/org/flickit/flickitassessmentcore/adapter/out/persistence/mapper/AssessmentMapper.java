package org.flickit.flickitassessmentcore.adapter.out.persistence.mapper;

import org.flickit.flickitassessmentcore.adapter.out.persistence.entity.AssessmentColorEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.entity.AssessmentEntity;
import org.flickit.flickitassessmentcore.domain.Assessment;
import org.flickit.flickitassessmentcore.domain.AssessmentColor;
import org.flickit.flickitassessmentcore.domain.AssessmentKit;
import org.flickit.flickitassessmentcore.domain.MaturityLevel;

public class AssessmentMapper {
    public Assessment mapToDomainModel(AssessmentEntity assessmentEntity) {
        return new Assessment(assessmentEntity.getId(),
            assessmentEntity.getCode(),
            assessmentEntity.getTitle(),
            assessmentEntity.getDescription(),
            assessmentEntity.getCreationTime(),
            assessmentEntity.getLastModificationDate(),
            new AssessmentKit(assessmentEntity.getAssessmentKitId()),
            new AssessmentColor(), // TODO
//            assessmentEntity.getColor(),
            assessmentEntity.getSpaceId(),
            new MaturityLevel(assessmentEntity.getMaturityLevelId()),
            assessmentEntity.getAssessmentResults(),
            assessmentEntity.getEvidences());
    }

    public AssessmentEntity mapToJpaEntity(Assessment assessment) {
        return new AssessmentEntity(assessment.getId(),
            assessment.getCode(),
            assessment.getTitle(),
            assessment.getDescription(),
            assessment.getCreationTime(),
            assessment.getLastModificationDate(),
            assessment.getAssessmentKit().getId(),
            new AssessmentColorEntity(), // TODO
//            assessment.getColor(),
            assessment.getSpaceId(),
            assessment.getMaturityLevel().getId());
    }
}
