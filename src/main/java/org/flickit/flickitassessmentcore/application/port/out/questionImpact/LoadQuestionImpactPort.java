package org.flickit.flickitassessmentcore.application.port.out.questionImpact;

import org.flickit.flickitassessmentcore.domain.QuestionImpact;

public interface LoadQuestionImpactPort {

    Result loadQuestionImpact(Param param);

    record Param(Long id) {}

    record Result(QuestionImpact questionImpact) {}
}
