package org.flickit.assessment.users.application.port.out.expertgroupaccess;

import java.util.UUID;

public interface CheckInviteTokenValidationPort {

    void checkToken(long expertGroupId, UUID userId, UUID token);
}
