package org.flickit.assessment.kit.application.domain.dsl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@Jacksonized
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class QuestionDslModel extends BaseDslModel {

    String questionnaireCode;
    List<QuestionImpactDslModel> questionImpacts;
    @JsonProperty("answers")
    List<AnswerOptionDslModel> answerOptions;
    boolean mayNotBeApplicable;
    boolean advisable;
    Long answerRangeId;
}
