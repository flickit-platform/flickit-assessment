package org.flickit.assessment.core.application.port.in.assessmentreport;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_REPORT_METADATA_ASSESSMENT_ID_NOT_NULL;

public interface GetAssessmentReportMetadataUseCase {

    Result getAssessmentReportMetadata(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_ASSESSMENT_REPORT_METADATA_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(UUID assessmentId, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(Metadata metadata, boolean published) {

        public record Metadata(String intro,
                               String prosAndCons,
                               String steps,
                               String participants) {
        }
    }
}
