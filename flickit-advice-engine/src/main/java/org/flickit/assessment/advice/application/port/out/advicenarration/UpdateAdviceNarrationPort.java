package org.flickit.assessment.advice.application.port.out.advicenarration;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateAdviceNarrationPort {

    void updateAssessorNarration(AssessorNarrationParam adviceNarration);

    record AssessorNarrationParam(UUID id,
                                  String narration,
                                  LocalDateTime narrationTime,
                                  UUID createdBy) {
    }

    void updateAiNarration(AiNarrationParam adviceNarration);

    record AiNarrationParam(UUID id,
                            String narration,
                            LocalDateTime narrationTime) {
    }
}
