package org.flickit.assessment.core.application.port.out.question;

import org.flickit.assessment.core.domain.Question;

import java.util.Set;

public interface LoadQuestionsByQualityAttributePort {
    Set<Question> loadQuestionsByQualityAttributeId(Long qualityAttributeId);
}
