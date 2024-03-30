package org.flickit.assessment.users.application.port.out.expertgroupaccess;

import java.util.UUID;

public interface CheckInviteTokenExpiryPort {

    boolean isInviteTokenValid(UUID inviteToken);
}
