package org.flickit.assessment.core.adapter.out.persistence.assessmentsubjectvalue;

import org.flickit.assessment.core.domain.AssessmentSubject;
import org.flickit.assessment.core.domain.AssessmentSubjectValue;
import org.flickit.assessment.core.domain.MaturityLevel;

public class AssessmentSubjectValueMapper {

    public static AssessmentSubjectValue mapToDomainModel(AssessmentSubjectValueJpaEntity qualityAttributeValueEntity) {
        return new AssessmentSubjectValue(
            qualityAttributeValueEntity.getId(),
            new AssessmentSubject(qualityAttributeValueEntity.getAssessmentSubjectId()),
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
