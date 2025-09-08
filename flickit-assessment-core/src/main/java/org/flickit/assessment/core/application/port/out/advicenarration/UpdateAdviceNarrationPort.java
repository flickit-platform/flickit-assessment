package org.flickit.assessment.core.application.port.out.advicenarration;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateAdviceNarrationPort {

    void updateAssessorNarration(AssessorNarrationParam adviceNarration);

    record AssessorNarrationParam(UUID id,
                                  String narration,
                                  boolean approved,
                                  LocalDateTime narrationTime,
                                  UUID createdBy) {
    }

    void updateAiNarration(AiNarrationParam adviceNarration);

    record AiNarrationParam(UUID id,
                            String narration,
                            boolean approved,
                            LocalDateTime narrationTime) {
    }

    void approve(UUID assessmentId, LocalDateTime lastModificationTime);
}
