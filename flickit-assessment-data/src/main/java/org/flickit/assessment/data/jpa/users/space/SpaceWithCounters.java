package org.flickit.assessment.data.jpa.users.space;

public interface SpaceWithCounters {

    SpaceJpaEntity getSpace();

    String getOwnerName();

    int getMembersCount();

    int getAssessmentsCount();
}
