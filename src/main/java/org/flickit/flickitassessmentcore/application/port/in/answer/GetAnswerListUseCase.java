package org.flickit.flickitassessmentcore.application.port.in.answer;

import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.flickit.flickitassessmentcore.common.SelfValidating;

import java.util.List;
import java.util.UUID;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.GET_ANSWER_LIST_ASSESSMENT_ID_NOTNULL;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.GET_ANSWER_LIST_QUESTIONNAIRE_ID_NOTNULL;

public interface GetAnswerListUseCase {

    Result getAnswerList(Param param);

    @Value
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_ANSWER_LIST_ASSESSMENT_ID_NOTNULL)
        UUID assessmentId;

        @NotNull(message = GET_ANSWER_LIST_QUESTIONNAIRE_ID_NOTNULL)
        Long questionnaireId;

        public Param(UUID assessmentId, Long questionnaireId) {
            this.assessmentId = assessmentId;
            this.questionnaireId = questionnaireId;
            this.validateSelf();
        }
    }

    record Result(List<AnswerItem> answers){
    }

    record AnswerItem(
        UUID id,
        Long questionId,
        Long answerOptionId,
        Boolean isNotApplicable){
    }
}
