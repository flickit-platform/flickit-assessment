package org.flickit.assessment.core.application.port.out.adviceitem;

import org.flickit.assessment.common.application.domain.adviceitem.CostLevel;
import org.flickit.assessment.common.application.domain.adviceitem.ImpactLevel;
import org.flickit.assessment.common.application.domain.adviceitem.PriorityLevel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CreateAdviceItemPort {

    UUID persist(Param param, UUID assessmentResultId);

    void persistAll(List<Param> params, UUID assessmentResultId);

    record Param(String title,
                 String description,
                 CostLevel cost,
                 PriorityLevel priority,
                 ImpactLevel impact,
                 LocalDateTime creationTime,
                 UUID createdBy) {
    }
}
