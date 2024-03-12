package org.flickit.assessment.core.application.port.out.answeroption;

import org.flickit.assessment.core.application.domain.AnswerOption;

import java.util.Optional;

public interface LoadAnswerOptionPort {

    Optional<AnswerOption> loadById(Long id);
}
