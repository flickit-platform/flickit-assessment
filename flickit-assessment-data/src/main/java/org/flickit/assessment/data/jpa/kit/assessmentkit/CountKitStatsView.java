package org.flickit.assessment.data.jpa.kit.assessmentkit;

public interface CountKitStatsView {

    Long getId();

    Integer getQuestionnaireCount();

    Integer getAttributeCount();

    Integer getQuestionCount();

    Integer getMaturityLevelCount();

    Integer getLikeCount();

    Integer getAssessmentCount();
}
