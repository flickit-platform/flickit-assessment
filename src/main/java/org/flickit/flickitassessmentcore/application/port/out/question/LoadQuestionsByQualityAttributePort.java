package org.flickit.flickitassessmentcore.application.port.out.question;

import org.flickit.flickitassessmentcore.domain.Question;

import java.util.Set;

public interface LoadQuestionsByQualityAttributePort {
    Set<Question> loadQuestionsByQualityAttributeId(Long qualityAttributeId);
}
