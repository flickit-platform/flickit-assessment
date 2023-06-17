package org.flickit.flickitassessmentcore.application.port.in.answer;

import java.util.UUID;

public interface SubmitAnswerUseCase {

    UUID submitAnswer(SubmitAnswerCommand command);
}
