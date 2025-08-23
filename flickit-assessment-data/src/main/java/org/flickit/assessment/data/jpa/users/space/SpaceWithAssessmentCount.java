package org.flickit.assessment.data.jpa.users.space;

public interface SpaceWithAssessmentCount {

    SpaceJpaEntity getSpace();

    int getAssessmentsCount();
}
