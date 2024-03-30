package org.flickit.assessment.users.application.port.out.expertgroupaccess;

import java.util.UUID;

public interface CheckInviteInputDataValidationPort {

    void checkToken(long expertGroupId, UUID userId, UUID token);
}
