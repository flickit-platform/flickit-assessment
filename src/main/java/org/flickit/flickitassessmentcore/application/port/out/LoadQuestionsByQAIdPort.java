package org.flickit.flickitassessmentcore.application.port.out;

import org.flickit.flickitassessmentcore.domain.Question;

import java.util.Set;

public interface LoadQuestionsByQAIdPort {
    Set<Question> loadQuestionsByQualityAttributeId(Long qualityAttributeId);
}
