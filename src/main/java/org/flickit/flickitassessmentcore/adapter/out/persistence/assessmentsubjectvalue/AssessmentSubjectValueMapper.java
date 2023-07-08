package org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentsubjectvalue;

import org.flickit.flickitassessmentcore.domain.AssessmentSubject;
import org.flickit.flickitassessmentcore.domain.AssessmentSubjectValue;
import org.flickit.flickitassessmentcore.domain.MaturityLevel;

public class AssessmentSubjectValueMapper {

    public static AssessmentSubjectValue mapToDomainModel(AssessmentSubjectValueJpaEntity qualityAttributeValueEntity) {
        return new AssessmentSubjectValue(
            qualityAttributeValueEntity.getId(),
            new AssessmentSubject(qualityAttributeValueEntity.getSubjectId()),
            new MaturityLevel(qualityAttributeValueEntity.getMaturityLevelId())
        );
    }

    public static AssessmentSubjectValueJpaEntity mapToJpaEntity(AssessmentSubjectValue qualityAttributeValue) {
        return new AssessmentSubjectValueJpaEntity(
            qualityAttributeValue.getId(),
            null, // TODO
            qualityAttributeValue.getAssessmentSubject().getId(),
            qualityAttributeValue.getMaturityLevel().getId()
        );
    }


}
