package org.flickit.assessment.core.application.port.out.answeroption;

import org.flickit.assessment.core.application.domain.AnswerOption;

import java.util.List;
import java.util.Optional;

public interface LoadAnswerOptionPort {

    Optional<AnswerOption> load(long answerOptionId, long kitVersionId);

    List<AnswerOption> loadAll(long questionId, long kitVersionId);
}
