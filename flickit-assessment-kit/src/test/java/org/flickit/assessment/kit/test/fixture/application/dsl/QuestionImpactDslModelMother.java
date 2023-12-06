package org.flickit.assessment.kit.test.fixture.application.dsl;

import org.flickit.assessment.kit.application.domain.dsl.MaturityLevelDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionImpactDslModel;

import java.util.Map;

public class QuestionImpactDslModelMother {

    public static QuestionImpactDslModel questionImpactDslModel(
        String attributeCode,
        MaturityLevelDslModel maturityLevel,
        QuestionDslModel question,
        Map<Integer, Double> optionsIndextoValueMap,
        Integer weight) {
        return QuestionImpactDslModel.builder()
            .attributeCode(attributeCode)
            .maturityLevel(maturityLevel)
            .question(question)
            .optionsIndextoValueMap(optionsIndextoValueMap)
            .weight(weight)
            .build();
    }
}
