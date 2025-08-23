package org.flickit.assessment.kit.application.domain.dsl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@Builder
@Jacksonized
public class AssessmentKitDslModel {

    @JsonProperty("questionnaireModels")
    List<QuestionnaireDslModel> questionnaires;

    @JsonProperty("attributeModels")
    List<AttributeDslModel> attributes;

    @JsonProperty("questionModels")
    List<QuestionDslModel> questions;

    @JsonProperty("subjectModels")
    List<SubjectDslModel> subjects;

    @JsonProperty("levelModels")
    List<MaturityLevelDslModel> maturityLevels;

    @JsonProperty("answerRangeModels")
    List<AnswerRangeDslModel> answerRanges;

    @JsonProperty("hasError")
    boolean hasError;
}
