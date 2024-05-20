package org.flickit.assessment.core.application.port.out.answer;

import org.flickit.assessment.core.application.domain.Answer;

import java.util.List;
import java.util.UUID;

public interface LoadQuestionsAnswerListPort {

    List<Answer> loadByQuestionIds(UUID assessmentId, List<Long> questionIds);
}
