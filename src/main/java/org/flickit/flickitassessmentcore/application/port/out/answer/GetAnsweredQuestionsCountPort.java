package org.flickit.flickitassessmentcore.application.port.out.answer;

import org.flickit.flickitassessmentcore.application.port.in.answer.GetAnsweredQuestionsCountUseCase;

import java.util.UUID;

public interface GetAnsweredQuestionsCountPort {
    GetAnsweredQuestionsCountUseCase.Progress<UUID> getAnsweredQuestionsCountById(UUID assessmentId);
}
