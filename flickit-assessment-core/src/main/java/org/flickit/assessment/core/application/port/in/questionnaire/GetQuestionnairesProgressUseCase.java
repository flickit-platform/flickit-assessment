package org.flickit.assessment.core.application.port.in.questionnaire;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_QUESTIONNAIRES_PROGRESS_ASSESSMENT_ID_NOT_NULL;

public interface GetQuestionnairesProgressUseCase {

    Result getQuestionnairesProgress(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_QUESTIONNAIRES_PROGRESS_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(UUID assessmentId, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(List<QuestionnaireProgress> questionnairesProgress){
    }

    record QuestionnaireProgress(Long id, Integer answersCount, Integer nextQuestion) {
    }
}
