package org.flickit.assessment.core.application.port.in.answer;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.core.application.domain.ConfidenceLevel;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

public interface GetAnswerListUseCase {

    PaginatedResponse<AnswerListItem> getAnswerList(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_ANSWER_LIST_ASSESSMENT_ID_NOTNULL)
        UUID assessmentId;

        @NotNull(message = GET_ANSWER_LIST_QUESTIONNAIRE_ID_NOTNULL)
        Long questionnaireId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Min(value = 1, message = GET_ANSWER_LIST_SIZE_MIN)
        @Max(value = 100, message = GET_ANSWER_LIST_SIZE_MAX)
        int size;

        @Min(value = 0, message = GET_ANSWER_LIST_PAGE_MIN)
        int page;

        public Param(UUID assessmentId, Long questionnaireId, UUID currentUserId, int size, int page) {
            this.assessmentId = assessmentId;
            this.questionnaireId = questionnaireId;
            this.currentUserId = currentUserId;
            this.size = size;
            this.page = page;
            this.validateSelf();
        }
    }

    record AnswerListItem(
        UUID id,
        Long questionId,
        Long answerOptionId,
        ConfidenceLevel confidenceLevel,
        Boolean isNotApplicable){
    }
}
