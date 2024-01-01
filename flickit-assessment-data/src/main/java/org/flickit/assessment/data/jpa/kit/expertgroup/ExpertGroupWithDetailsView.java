package org.flickit.assessment.data.jpa.kit.expertgroup;

import org.flickit.assessment.data.jpa.kit.user.UserJpaEntity;

import java.util.List;
import java.util.UUID;

public interface ExpertGroupWithDetailsView {
    Long getId();

    String getName();

    String getPicture();

    String getBio();

    Integer getPublishedKitsCount();
    Boolean getEditable();

    UUID getOwnerId();

    List<UserJpaEntity> getUsers();
}
