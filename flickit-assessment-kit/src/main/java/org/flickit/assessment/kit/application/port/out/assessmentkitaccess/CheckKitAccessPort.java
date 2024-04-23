package org.flickit.assessment.kit.application.port.out.assessmentkitaccess;

import java.util.UUID;

public interface CheckKitAccessPort {

    boolean checkHasAccess(long kitId, UUID userId);
}
