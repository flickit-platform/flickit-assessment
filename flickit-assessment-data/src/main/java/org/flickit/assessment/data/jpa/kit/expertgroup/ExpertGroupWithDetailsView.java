package org.flickit.assessment.data.jpa.kit.expertgroup;

import java.util.UUID;

public interface ExpertGroupWithDetailsView {

    Long getId();

    String getName();

    String getPicture();

    String getWebsite();

    String getBio();

    Integer getPublishedKitsCount();

    UUID getOwnerId();

    Integer getMembersCount();
}
