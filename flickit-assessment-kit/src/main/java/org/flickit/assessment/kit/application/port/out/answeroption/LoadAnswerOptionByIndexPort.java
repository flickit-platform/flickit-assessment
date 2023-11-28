package org.flickit.assessment.kit.application.port.out.answeroption;

import org.flickit.assessment.kit.application.domain.AnswerOption;

public interface LoadAnswerOptionByIndexPort {

    AnswerOption loadByIndex(int index, Long questionId);
}
