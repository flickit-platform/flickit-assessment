package org.flickit.flickitassessmentcore.application.port.in.assessment;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.flickitassessmentcore.common.SelfValidating;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.*;

public interface CountUseCase {

    Result count(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = COUNT_ASSESSMENTS_ASSESSMENT_KIT_ID_NOT_NULL)
        Long assessmentKitId;

        @NotNull(message = COUNT_ASSESSMENTS_INCLUDE_DELETED_NOT_NULL)
        Boolean includeDeleted;

        @NotNull(message = COUNT_ASSESSMENTS_INCLUDE_NOT_DELETED_NOT_NULL)
        Boolean includeNotDeleted;

        public Param(Long assessmentKitId, Boolean includeDeleted, Boolean includeNotDeleted) {
            this.assessmentKitId = assessmentKitId;
            this.includeDeleted = includeDeleted;
            this.includeNotDeleted = includeNotDeleted;
            this.validateSelf();
        }
    }

    record Result(int count) {
    }

}

