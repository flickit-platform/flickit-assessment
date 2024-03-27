package org.flickit.assessment.data.jpa.users.expertgroup;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ExpertGroupMembersView {

    UUID getId();
    String getEmail();
    String getDisplayName();
    String getBio();
    String getPicture();
    String getLinkedin();
    int getStatus();
    LocalDateTime getInviteExpirationDate();
}
