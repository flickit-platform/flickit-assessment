package org.flickit.assessment.data.jpa.core.assessment;

public interface AssessmentListItemView {

    AssessmentJpaEntity getAssessment();

    Long getMaturityLevelId();

    boolean getIsCalculateValid();

    boolean getIsConfidenceValid();
}
