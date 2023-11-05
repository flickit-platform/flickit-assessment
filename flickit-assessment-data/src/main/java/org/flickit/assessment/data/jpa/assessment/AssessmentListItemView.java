package org.flickit.assessment.data.jpa.assessment;

public interface AssessmentListItemView {

    AssessmentJpaEntity getAssessment();

    Long getMaturityLevelId();

    boolean getIsCalculateValid();
}
