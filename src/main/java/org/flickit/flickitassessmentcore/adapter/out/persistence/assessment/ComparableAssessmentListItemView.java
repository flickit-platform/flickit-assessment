package org.flickit.flickitassessmentcore.adapter.out.persistence.assessment;

public interface ComparableAssessmentListItemView {

    AssessmentJpaEntity getAssessment();

    Long getMaturityLevelId();

    boolean getIsCalculateValid();
}
