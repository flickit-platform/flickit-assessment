package org.flickit.assessment.data.jpa.kit.expertgroup;

import java.util.UUID;

public interface MembersView {

    UUID getId();
    String getEmail();
    String getDisplayName();
    String getBio();
    String getPicture();
    String getLinkedin();
}
