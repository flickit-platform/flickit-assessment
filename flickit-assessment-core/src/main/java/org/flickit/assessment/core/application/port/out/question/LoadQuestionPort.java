package org.flickit.assessment.core.application.port.out.question;

import org.flickit.assessment.core.application.domain.Question;

public interface LoadQuestionPort {

    Question loadByIdAndKitVersionId(Long id, Long kitVersionId);
}
