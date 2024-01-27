package org.flickit.assessment.kit.application.port.out.kitdsl;

import java.util.UUID;

public interface CheckIsMemberPort {

    Boolean checkIsMemberByKitId(long kitId, UUID currentUserId);
}
