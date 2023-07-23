package org.flickit.flickitassessmentcore.application.port.out.questionImpact;

import org.flickit.flickitassessmentcore.domain.QuestionImpact;

public interface LoadQuestionImpactPort {

    Result load(Long id);

    record Result(QuestionImpact questionImpact) {}
}
