package org.flickit.assessment.data.jpa.users.expertgroup;

import java.util.UUID;

public interface ExpertGroupWithDetailsView {

    Long getId();

    String getTitle();

    String getPicture();

    String getWebsite();

    String getBio();

    Integer getPublishedKitsCount();

    UUID getOwnerId();
}
