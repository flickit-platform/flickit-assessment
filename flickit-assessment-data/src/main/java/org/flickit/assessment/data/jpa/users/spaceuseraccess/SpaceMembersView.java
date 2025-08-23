package org.flickit.assessment.data.jpa.users.spaceuseraccess;

import java.util.UUID;

public interface SpaceMembersView {

    UUID getId();

    String getEmail();

    String getDisplayName();

    String getBio();

    String getPicture();

    String getLinkedin();
}
