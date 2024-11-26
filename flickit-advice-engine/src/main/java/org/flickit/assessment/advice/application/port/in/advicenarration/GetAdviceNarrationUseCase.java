package org.flickit.assessment.advice.application.port.in.advicenarration;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.ID;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.advice.common.ErrorMessageKey.GET_ADVICE_NARRATION_ASSESSMENT_ID_NOT_NULL;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;

public interface GetAdviceNarrationUseCase {

    Result getAdviceNarration(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_ADVICE_NARRATION_ASSESSMENT_ID_NOT_NULL)
        ID assessmentId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        ID currentUserId;

        public Param(ID assessmentId, ID currentUserId) {
            this.assessmentId = assessmentId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(AdviceNarration aiNarration, AdviceNarration assessorNarration, boolean editable, boolean aiEnabled) {

        public record AdviceNarration(String narration, LocalDateTime creationTime) {}
    }
}
