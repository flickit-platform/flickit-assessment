package org.flickit.assessment.core.application.port.out.question;

import org.flickit.assessment.core.application.domain.Question;

import java.util.Optional;
import java.util.UUID;

public interface LoadQuestionPort {

    Optional<Question> loadByRefNum(long kitVersionId, UUID refNum);
}
