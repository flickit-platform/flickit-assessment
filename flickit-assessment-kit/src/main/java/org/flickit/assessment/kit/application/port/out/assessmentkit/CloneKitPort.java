package org.flickit.assessment.kit.application.port.out.assessmentkit;

import java.util.UUID;

public interface CloneKitPort {

    void cloneKit(long activeKitVersionId, long updatingKitVersionId, UUID currentUserId);
}
