package org.flickit.assessment.kit.application.domain.excel;

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
public class QuestionExcelModel extends BaseExcelModel {

    String questionnaireCode;
    List<QuestionImpactExcelModel> questionImpacts;
    @JsonProperty("answers")
    List<AnswerOptionExcelModel> answerOptions;
    boolean mayNotBeApplicable;
    boolean advisable;
}
