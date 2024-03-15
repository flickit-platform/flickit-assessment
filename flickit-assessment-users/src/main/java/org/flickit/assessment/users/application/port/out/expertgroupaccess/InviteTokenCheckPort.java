package org.flickit.assessment.users.application.port.out.expertgroupaccess;

import java.util.UUID;

public interface InviteTokenCheckPort {

    boolean checkInviteToken(UUID inviteToke);
}
