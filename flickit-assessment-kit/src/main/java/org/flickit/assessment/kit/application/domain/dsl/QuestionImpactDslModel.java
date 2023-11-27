package org.flickit.assessment.kit.application.domain.dsl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class QuestionImpactDslModel {

    private String attributeCode;
    @JsonProperty("level")
    private MaturityLevelDslModel maturityLevel; //TODO can be converted to maturityLevelCode
    private QuestionDslModel question; //TODO always is null
    @JsonProperty("optionValues")
    private Map<Integer, Double> optionsIndextoValueMap;
    private Integer weight;

}
