package org.flickit.assessment.kit.application.domain.excel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@Builder
@Jacksonized
public class KitExcelModel {

    @JsonProperty("questionnaireModels")
    List<QuestionnaireExcelModel> questionnaires;

    @JsonProperty("attributeModels")
    List<AttributeExcelModel> attributes;

    @JsonProperty("questionModels")
    List<QuestionExcelModel> questions;

    @JsonProperty("subjectModels")
    List<SubjectExcelModel> subjects;

    @JsonProperty("levelModels")
    List<MaturityLevelExcelModel> maturityLevels;

    @JsonProperty("hasError")
    boolean hasError;
}
