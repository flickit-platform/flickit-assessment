package org.flickit.assessment.data.jpa.users.space;

public interface SpaceWithDetails {

    SpaceJpaEntity getSpace();

    String getOwnerName();

    int getMembersCount();

    int getAssessmentsCount();
}
