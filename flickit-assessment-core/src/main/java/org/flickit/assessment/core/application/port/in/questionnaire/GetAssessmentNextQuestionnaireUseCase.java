package org.flickit.assessment.core.application.port.in.questionnaire;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_NEXT_QUESTIONNAIRE_ASSESSMENT_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_NEXT_QUESTIONNAIRE_QUESTIONNAIRE_ID_NOT_NULL;

public interface GetAssessmentNextQuestionnaireUseCase {

    Result getNextQuestionnaire(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_ASSESSMENT_NEXT_QUESTIONNAIRE_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = GET_ASSESSMENT_NEXT_QUESTIONNAIRE_QUESTIONNAIRE_ID_NOT_NULL)
        Long questionnaireId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(UUID assessmentId, Long questionnaireId, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.questionnaireId = questionnaireId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    sealed interface Result permits Result.Found, Result.NotFound {

        record Found(long id, int index, String title, Integer questionIndex) implements Result {
        }

        record NotFound() implements Result {

            public static final NotFound INSTANCE = new NotFound();
        }
    }
}
