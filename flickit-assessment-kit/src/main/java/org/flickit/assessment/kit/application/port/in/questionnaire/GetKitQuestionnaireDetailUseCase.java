package org.flickit.assessment.kit.application.port.in.questionnaire;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.kit.application.domain.Question;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface GetKitQuestionnaireDetailUseCase {

    Result getKitQuestionnaireDetail(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_KIT_QUESTIONNAIRE_KIT_ID_NOT_NULL)
        Long kitId;

        @NotNull(message = GET_KIT_QUESTIONNAIRE_QUESTIONNAIRE_ID_NOT_NULL)
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

    record Result(int questionsCount, List<String> relatedSubjects, String description, List<Question> questions) {}
}
