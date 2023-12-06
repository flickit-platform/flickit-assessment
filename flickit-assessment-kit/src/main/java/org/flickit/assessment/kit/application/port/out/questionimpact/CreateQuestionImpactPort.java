package org.flickit.assessment.kit.application.port.out.questionimpact;

import org.flickit.assessment.kit.application.domain.QuestionImpact;

public interface CreateQuestionImpactPort {

    Long persist(QuestionImpact impact);
}
