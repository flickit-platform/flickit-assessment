package org.flickit.flickitassessmentcore.adapter.out.persistence.subjectvalue;

import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.flickitassessmentcore.domain.AssessmentSubject;
import org.flickit.flickitassessmentcore.domain.SubjectValue;
import org.flickit.flickitassessmentcore.domain.MaturityLevel;

public class SubjectValueMapper {

    public static SubjectValueJpaEntity mapToJpaEntity(Long subjectId){
        return new SubjectValueJpaEntity(
            null,
            null,
            subjectId,
            null
        );
    }

    public static SubjectValue mapToDomainModel(SubjectValueJpaEntity subjectValueJpaEntity) {
        return new SubjectValue(
            subjectValueJpaEntity.getId(),
            new AssessmentSubject(subjectValueJpaEntity.getSubjectId()),
            new MaturityLevel(subjectValueJpaEntity.getMaturityLevelId()),
            subjectValueJpaEntity.getAssessmentResult() != null ? subjectValueJpaEntity.getAssessmentResult().getId() : null
        );
    }

    public static SubjectValueJpaEntity mapToJpaEntity(SubjectValue subjectValue) {
        return new SubjectValueJpaEntity(
            subjectValue.getId(),
            new AssessmentResultJpaEntity(subjectValue.getResultId()),
            subjectValue.getAssessmentSubject().getId(),
            subjectValue.getMaturityLevel().getId()
        );
    }
}
