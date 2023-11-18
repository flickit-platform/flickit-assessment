package org.flickit.assessment.kit.application.domain.dsl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class QuestionDslModel extends BaseDslModel {

    private String questionnaireCode;
    private List<QuestionImpactDslModel> questionImpacts;
    @JsonProperty("answers")
    private List<AnswerOptionDslModel> answerOptions;
    private boolean mayNotBeApplicable;

}
