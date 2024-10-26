package org.flickit.assessment.kit.application.port.out.assessmentkit;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CloneKitPort {

    void cloneKit(Param param);

    record Param(long activeKitVersionId, long updatingKitVersionId, UUID clonedBy, LocalDateTime cloneTime) {}
}
