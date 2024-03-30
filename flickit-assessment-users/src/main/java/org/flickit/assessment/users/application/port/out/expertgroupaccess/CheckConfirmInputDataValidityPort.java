package org.flickit.assessment.users.application.port.out.expertgroupaccess;

import java.util.UUID;

public interface CheckConfirmInputDataValidityPort {

    boolean checkInputData(long expertGroupId, UUID userId, UUID token);
}
