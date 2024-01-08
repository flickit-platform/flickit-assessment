package org.flickit.assessment.data.jpa.kit.expertgroup;

import java.util.UUID;

public interface ExpertGroupWithDetailsView {

    Long getId();

    String getName();

    String getPicture();

    String getBio();

    Integer getPublishedKitsCount();

    Boolean getEditable();

    UUID getOwnerId();

    Integer getMembersCount();

}
