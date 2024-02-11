package org.flickit.assessment.kit.application.port.out.assessmentkit;

import java.time.LocalDateTime;

public interface UpdateKitLastMajorModificationTimePort {

    void updateLastMajorModificationTime(Long kitId, LocalDateTime lastMajorModificationTime);
}
