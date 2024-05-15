package org.flickit.assessment.core.application.port.out.answer;

import org.flickit.assessment.core.application.domain.Answer;

import java.util.List;
import java.util.UUID;

public interface LoadQuestionnaireAnswerListPort {

    List<Answer> loadQuestionnaireAnswers(UUID assessmentId, long questionnaireId, int size, int page);
}
