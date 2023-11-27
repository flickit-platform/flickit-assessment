package org.flickit.assessment.kit.application.port.out.question;

import org.flickit.assessment.kit.application.domain.Question;

public interface LoadQuestionByCodePort {

    Question loadByCode(String code);
}
