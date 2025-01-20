package org.flickit.assessment.core.application.port.in.assessmentreport;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

public interface CreateAssessmentReportMetadataUseCase {

    void createReportMetadata(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = CREATE_ASSESSMENT_REPORT_METADATA_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = CREATE_ASSESSMENT_REPORT_METADATA_METADATA_NOT_NULL)
        MetadataParam metadata;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(UUID assessmentId, MetadataParam metadata, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.metadata = metadata;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    @Value
    @EqualsAndHashCode(callSuper = true)
    class MetadataParam extends SelfValidating<MetadataParam> {

        @Size(max = 1000, message = CREATE_ASSESSMENT_REPORT_METADATA_INTRO_SIZE_MAX)
        String intro;

        @Size(max = 1000, message = CREATE_ASSESSMENT_REPORT_METADATA_PROS_AND_CONS_SIZE_MAX)
        String prosAndCons;

        String steps;

        String participants;

        @Builder
        public MetadataParam(String into, String prosAndCons, String steps, String participants) {
            this.intro = into;
            this.prosAndCons = prosAndCons;
            this.steps = steps;
            this.participants = participants;
            this.validateSelf();
        }
    }
}
