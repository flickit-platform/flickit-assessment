package org.flickit.assessment.users.application.port.out.expertgroupaccess;

import java.util.UUID;

public interface CheckInviteInputDataValidityPort {

    boolean checkInputData(long expertGroupId, UUID userId, UUID token);
}
