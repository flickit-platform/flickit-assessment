package org.flickit.assessment.kit.test.fixture.application.dsl.model.mother;

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
        QuestionImpactDslModel dslImpact = new QuestionImpactDslModel();
        dslImpact.setAttributeCode(attributeCode);
        dslImpact.setMaturityLevel(maturityLevel);
        dslImpact.setQuestion(question);
        dslImpact.setOptionsIndextoValueMap(optionsIndextoValueMap);
        dslImpact.setWeight(weight);
        return dslImpact;
    }
}
