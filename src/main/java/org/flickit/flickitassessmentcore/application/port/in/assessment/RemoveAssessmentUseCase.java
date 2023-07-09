package org.flickit.flickitassessmentcore.application.port.in.assessment;

import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.flickit.flickitassessmentcore.common.SelfValidating;

import java.util.UUID;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.REMOVE_ASSESSMENT_ID_NOT_NULL;

public interface RemoveAssessmentUseCase {

    void removeAssessment(Param param);

    @Value
    class Param extends SelfValidating<Param> {

        @NotNull(message = REMOVE_ASSESSMENT_ID_NOT_NULL)
        UUID id;

        public Param(UUID id) {
            this.id = id;
            this.validateSelf();
        }
    }
}
