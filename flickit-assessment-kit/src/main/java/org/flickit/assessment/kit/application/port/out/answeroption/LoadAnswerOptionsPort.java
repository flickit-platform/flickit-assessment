package org.flickit.assessment.kit.application.port.out.answeroption;

import org.flickit.assessment.kit.application.domain.AnswerOption;

import java.util.List;

public interface LoadAnswerOptionsPort {

    List<AnswerOption> loadByQuestionId(Long questionId, Long kitVersionId);
}
