package org.flickit.flickitassessmentcore.application.port.out.question;

import org.flickit.flickitassessmentcore.domain.Question;

import java.util.Set;

public interface LoadQuestionsByQualityAttributePort {
    Result loadQuestionsByQualityAttributeId(Param param);

    record Param(Long qualityAttributeId) {}

    record Result(Set<Question> questions) {}
}
