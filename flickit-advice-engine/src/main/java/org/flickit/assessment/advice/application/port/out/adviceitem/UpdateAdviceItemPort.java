package org.flickit.assessment.advice.application.port.out.adviceitem;

import org.flickit.assessment.common.application.domain.adviceitem.CostLevel;
import org.flickit.assessment.common.application.domain.adviceitem.ImpactLevel;
import org.flickit.assessment.common.application.domain.adviceitem.PriorityLevel;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateAdviceItemPort {

    void update(Param param);

    record Param(UUID id,
                 String title,
                 String description,
                 CostLevel cost,
                 PriorityLevel priority,
                 ImpactLevel impact,
                 LocalDateTime lastModificationTime,
                 UUID lastModifiedBy) {
    }
}
