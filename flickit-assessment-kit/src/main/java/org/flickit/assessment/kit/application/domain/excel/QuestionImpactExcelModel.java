package org.flickit.assessment.kit.application.domain.excel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class QuestionImpactExcelModel {

    String attributeCode;
    @JsonProperty("level")
    MaturityLevelExcelModel maturityLevel; //TODO can be converted to maturityLevelCode
    QuestionExcelModel question; //TODO always is null
    Integer weight;
}
