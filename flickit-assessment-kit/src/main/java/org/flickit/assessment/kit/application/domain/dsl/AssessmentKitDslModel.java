package org.flickit.assessment.kit.application.domain.dsl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AssessmentKitDslModel {

    @JsonProperty("questionnaireModels")
    private List<QuestionnaireDslModel> questionnaires;

    @JsonProperty("attributeModels")
    private List<AttributeDslModel> attributes;

    @JsonProperty("questionModels")
    private List<QuestionDslModel> questions;

    @JsonProperty("subjectModels")
    private List<SubjectDslModel> subjects;

    @JsonProperty("levelModels")
    private List<MaturityLevelDslModel> maturityLevels;

    @JsonProperty("hasError")
    private boolean hasError;
}
