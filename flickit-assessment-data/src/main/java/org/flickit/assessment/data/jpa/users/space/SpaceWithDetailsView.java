package org.flickit.assessment.data.jpa.users.space;

import java.time.LocalDateTime;
import java.util.UUID;

public interface SpaceWithDetailsView {

    Long getId();

    String getCode();

    String getTitle();

    UUID getOwnerId();

    LocalDateTime getLastModificationTime();

    int getMembersCount();

    int getAssessmentsCount();
}
