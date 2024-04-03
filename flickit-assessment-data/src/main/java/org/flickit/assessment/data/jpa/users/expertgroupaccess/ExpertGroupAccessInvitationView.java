package org.flickit.assessment.data.jpa.users.expertgroupaccess;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ExpertGroupAccessInvitationView {

    LocalDateTime getInviteExpirationDate();

    UUID getInviteToken();

    int getStatus();
}
