package org.flickit.assessment.kit.application.port.out.questionimpact;

import org.flickit.assessment.kit.application.domain.QuestionImpact;

import java.util.List;

public interface LoadQuestionImpactPort {

    List<QuestionImpact> loadAllByKitVersionId(long kitVersionId);
}
