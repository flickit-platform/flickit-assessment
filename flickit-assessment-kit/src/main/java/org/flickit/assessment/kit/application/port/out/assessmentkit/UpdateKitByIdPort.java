package org.flickit.assessment.kit.application.port.out.assessmentkit;

import java.time.LocalDateTime;

public interface UpdateKitByIdPort {

    void updateById(Long kitId, LocalDateTime lastEffectiveModificationTime);
}
