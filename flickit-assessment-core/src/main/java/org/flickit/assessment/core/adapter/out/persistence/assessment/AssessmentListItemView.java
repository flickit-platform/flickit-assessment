package org.flickit.assessment.core.adapter.out.persistence.assessment;

public interface AssessmentListItemView {

    AssessmentJpaEntity getAssessment();

    Long getMaturityLevelId();

    boolean getIsCalculateValid();
}
