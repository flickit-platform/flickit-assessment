package org.flickit.assessment.advice.application.port.out.adviceitem;

import org.flickit.assessment.advice.application.domain.adviceitem.CostLevel;
import org.flickit.assessment.advice.application.domain.adviceitem.ImpactLevel;
import org.flickit.assessment.advice.application.domain.adviceitem.PriorityLevel;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateAdviceItemPort {

    void updateAdviceItem(Param param);

    record Param(UUID id,
                 String title,
                 UUID assessmentResultId,
                 String description,
                 CostLevel cost,
                 PriorityLevel priority,
                 ImpactLevel impact,
                 LocalDateTime lastModificationTime,
                 UUID lastModifiedBy) {
    }
}
