package org.flickit.assessment.core.application.port.in.assessment;

import jakarta.validation.constraints.AssertTrue;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import static org.flickit.assessment.core.common.ErrorMessageKey.COUNT_ASSESSMENTS_KIT_ID_AND_SPACE_ID_NOT_NULL;

public interface CountAssessmentsUseCase {

    Result countAssessments(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        Long assessmentKitId;
        Long spaceId;
        boolean deleted;
        boolean notDeleted;
        boolean total;

        @AssertTrue(message = COUNT_ASSESSMENTS_KIT_ID_AND_SPACE_ID_NOT_NULL)
        private boolean isKitIdAndSpaceIdNotNull() {
            return !(assessmentKitId == null && spaceId == null);
        }

        public Param(Long assessmentKitId, Long spaceId, boolean deleted, boolean notDeleted, boolean total) {
            this.assessmentKitId = assessmentKitId;
            this.spaceId = spaceId;
            this.deleted = deleted;
            this.notDeleted = notDeleted;
            this.total = total;
            this.validateSelf();
        }
    }

    record Result(Integer totalCount, Integer deletedCount, Integer notDeletedCount) {
    }

}

