package org.flickit.flickitassessmentcore.application.port.in.assessment;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.flickitassessmentcore.common.SelfValidating;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.*;

public interface CountAssessmentsUseCase {

    Result countAssessments(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = COUNT_ASSESSMENTS_ASSESSMENT_KIT_ID_NOT_NULL)
        Long assessmentKitId;

        Boolean deleted;

        Boolean notDeleted;

        Boolean total;

        public Param(Long assessmentKitId, Boolean deleted, Boolean notDeleted, Boolean total) {
            this.assessmentKitId = assessmentKitId;
            this.deleted = deleted;
            this.notDeleted = notDeleted;
            this.total = total;
            this.validateSelf();
        }
    }

    record Result(Integer totalCount, Integer deletedCount, Integer notDeletedCount) {
    }

}

