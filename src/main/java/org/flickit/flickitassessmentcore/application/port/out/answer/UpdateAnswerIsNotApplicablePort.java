package org.flickit.flickitassessmentcore.application.port.out.answer;

import java.util.UUID;

public interface UpdateAnswerIsNotApplicablePort {
    void updateAnswerIsNotApplicableAndRemoveOptionById(Param param);

    record Param(UUID id, Boolean isNotApplicable){
    }
}
