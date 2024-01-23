package org.flickit.assessment.data.jpa.kit.expertgroup;

import java.util.UUID;

public interface ExpertGroupWithDetailsView {

    Long getId();

    String getTitle();

    String getPicture();

    String getBio();

    Integer getPublishedKitsCount();

    UUID getOwnerId();

    Integer getMembersCount();
}
