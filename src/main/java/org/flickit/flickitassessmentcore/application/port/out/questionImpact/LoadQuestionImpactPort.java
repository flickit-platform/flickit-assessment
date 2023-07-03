package org.flickit.flickitassessmentcore.application.port.out.questionImpact;

import org.flickit.flickitassessmentcore.domain.QuestionImpact;

public interface LoadQuestionImpactPort {

    QuestionImpact loadQuestionImpact(Long id);
}
