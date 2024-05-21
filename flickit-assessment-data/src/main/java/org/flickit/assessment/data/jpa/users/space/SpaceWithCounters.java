package org.flickit.assessment.data.jpa.users.space;

public interface SpaceWithCounters {

    SpaceJpaEntity getSpace();

    int getMembersCount();

    int getAssessmentsCount();
}
