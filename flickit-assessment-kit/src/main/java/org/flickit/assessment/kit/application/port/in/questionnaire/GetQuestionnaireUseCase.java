package org.flickit.assessment.kit.application.port.in.questionnaire;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitMinimalInfoUseCase;
import org.flickit.assessment.kit.application.port.out.questionnaire.LoadQuestionnairePort.Result;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface GetQuestionnaireUseCase {

    Result getQuestionnaire(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<GetKitMinimalInfoUseCase.Param> {

        @NotNull(message = GET_QUESTIONNAIRE_KIT_ID_NOT_NULL)
        Long kitId;

        @NotNull(message = GET_QUESTIONNAIRE_QUESTIONNAIRE_ID_NOT_NULL)
        Long questionnaireId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(Long kitId, Long questionnaireId, UUID currentUserId) {
            this.kitId = kitId;
            this.questionnaireId = questionnaireId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
