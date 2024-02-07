package org.flickit.assessment.core.application.port.out.assessmentkit;

import java.time.LocalDateTime;

public interface LoadKitLastMajorModificationTimePort {

    LocalDateTime loadLastMajorModificationTime(Long kitId);
}
