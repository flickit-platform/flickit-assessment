package org.flickit.assessment.core.application.port.in.questionnaire;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.core.application.domain.QuestionnaireListItem;

import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.*;

public interface GetAssessmentQuestionnaireListUseCase {

    PaginatedResponse<QuestionnaireListItem> getAssessmentQuestionnaireList(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_ASSESSMENT_QUESTIONNAIRE_LIST_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @Min(value = 1, message = GET_ASSESSMENT_QUESTIONNAIRE_LIST_SIZE_MIN)
        @Max(value = 50, message = GET_ASSESSMENT_QUESTIONNAIRE_LIST_SIZE_MAX)
        int size;

        @Min(value = 0, message = GET_ASSESSMENT_QUESTIONNAIRE_LIST_PAGE_MIN)
        int page;

        @NotNull(message = GET_ASSESSMENT_QUESTIONNAIRE_LIST_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(UUID assessmentId, int size, int page, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.size = size;
            this.page = page;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
