package org.flickit.assessment.core.application.port.out.answeroption;

import org.flickit.assessment.core.application.domain.AnswerOption;

import java.util.List;

public interface LoadAnswerOptionsByQuestionPort {

    List<AnswerOption> loadByQuestionId(Long questionId, Long kitVersionId);
}
