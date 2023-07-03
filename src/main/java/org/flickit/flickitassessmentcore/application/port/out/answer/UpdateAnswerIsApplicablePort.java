package org.flickit.flickitassessmentcore.application.port.out.answer;

import java.util.UUID;

public interface UpdateAnswerIsApplicablePort {
    void updateAnswerIsApplicableAndRemoveOptionById(Param param);

    record Param(UUID id, Boolean isApplicable){
    }
}
