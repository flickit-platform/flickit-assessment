package org.flickit.assessment.core.application.port.out.advicenarration;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CreateAdviceNarrationPort {

    void persist(Param param, UUID assessmentResultId);

    record Param(String aiNarration,
                 String assessorNarration,
                 boolean approved,
                 LocalDateTime aiNarrationTime,
                 LocalDateTime assessorNarrationTime,
                 UUID createdBy) {
    }
}
