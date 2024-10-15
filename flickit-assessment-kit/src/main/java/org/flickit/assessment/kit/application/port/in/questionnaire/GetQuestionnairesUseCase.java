package org.flickit.assessment.kit.application.port.in.questionnaire;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.domain.Questionnaire;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface GetQuestionnairesUseCase {

    PaginatedResponse<QuestionnaireListItem> getQuestionnaires(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_QUESTIONNAIRES_KIT_VERSION_ID_NOT_NULL)
        Long kitVersionId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Min(value = 0, message = GET_QUESTIONNAIRES_PAGE_MIN)
        int page;

        @Min(value = 1, message = GET_QUESTIONNAIRES_SIZE_MIN)
        @Max(value = 100, message = GET_QUESTIONNAIRES_SIZE_MAX)
        int size;

        @Builder
        public Param(Long kitVersionId, UUID currentUserId, int page, int size) {
            this.kitVersionId = kitVersionId;
            this.currentUserId = currentUserId;
            this.page = page;
            this.size = size;
            this.validateSelf();
        }
    }

    record QuestionnaireListItem(Questionnaire questionnaire, int questionsCount) {}
}
