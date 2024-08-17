package org.flickit.assessment.core.application.port.in.answerhistory;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.core.application.domain.ConfidenceLevel;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;


public interface GetAnswerHistoryListUseCase {

    PaginatedResponse<AnswerHistoryListItem> getAnswerHistoryList(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_ANSWER_HISTORY_LIST_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = GET_ANSWER_HISTORY_LIST_QUESTION_ID_NOT_NULL)
        Long questionId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Min(value = 1, message = GET_ANSWER_HISTORY_LIST_SIZE_MIN)
        @Max(value = 50, message = GET_ANSWER_HISTORY_LIST_SIZE_MAX)
        int size;

        @Min(value = 0, message = GET_ANSWER_HISTORY_LIST_PAGE_MIN)
        int page;

        public Param(UUID assessmentId, Long questionId, UUID currentUserId, int size, int page) {
            this.assessmentId = assessmentId;
            this.questionId = questionId;
            this.currentUserId = currentUserId;
            this.size = size;
            this.page = page;
            this.validateSelf();
        }
    }

    record AnswerHistoryListItem(
        Answer answer,
        LocalDateTime creationTime,
        User createdBy) {
    }

    record Answer(Option selectedOption, ConfidenceLevel confidenceLevel, Boolean isNotApplicable) {

        public static Answer of(org.flickit.assessment.core.application.domain.Answer answer) {
            return new Answer(answer.getSelectedOption() != null ? new Option(answer.getSelectedOption().getId()) : null,
                answer.getConfidenceLevelId() != null ? ConfidenceLevel.valueOfById(answer.getConfidenceLevelId()) : ConfidenceLevel.getDefault(),
                answer.getIsNotApplicable());
        }
    }

    record Option(long id) {
    }

    record User(UUID id, String displayName, String pictureLink) {
    }
}
