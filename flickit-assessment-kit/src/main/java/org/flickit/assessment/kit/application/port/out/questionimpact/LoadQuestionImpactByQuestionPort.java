package org.flickit.assessment.kit.application.port.out.questionimpact;

import org.flickit.assessment.kit.application.domain.AnswerOptionImpact;
import org.flickit.assessment.kit.application.domain.MaturityLevel;

import java.util.List;

public interface LoadQuestionImpactByQuestionPort {

    List<AttributeImpact> loadQuestionImpactByQuestionId(Long questionId);


    record AttributeImpact(Long attributeId,
                           List<AffectedLevel> affectedLevels) {
    }

    record AffectedLevel(MaturityLevel maturityLevel,
                         Integer weight,
                         List<AnswerOptionImpact> optionValues
    ) {
    }
}
