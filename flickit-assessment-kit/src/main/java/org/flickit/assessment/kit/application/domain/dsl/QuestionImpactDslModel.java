package org.flickit.assessment.kit.application.domain.dsl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Map;

@Value
@Builder
@Jacksonized
public class QuestionImpactDslModel {

    String attributeCode;
    @JsonProperty("level")
    MaturityLevelDslModel maturityLevel; //TODO can be converted to maturityLevelCode
    QuestionDslModel question; //TODO always is null
    @JsonProperty("optionValues")
    Map<Integer, Double> optionsIndextoValueMap;
    Integer weight;
}
