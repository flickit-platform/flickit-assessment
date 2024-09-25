package org.flickit.assessment.core.application.port.in.assessmentanalysis;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

public interface AddAssessmentAnalysisInputFileUseCase {

    Result addAssessmentAnalysisInputFile(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = ADD_ASSESSMENT_ANALYSIS_INPUT_FILE_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = ADD_ASSESSMENT_ANALYSIS_INPUT_FILE_INPUT_FILE_NOT_NULL)
        MultipartFile inputFile;

        @NotNull(message = ADD_ASSESSMENT_ANALYSIS_INPUT_FILE_ANALYSIS_TYPE_NOT_NULL)
        Integer analysisType;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(UUID assessmentId, MultipartFile inputFile, Integer analysisType, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.inputFile = inputFile;
            this.analysisType = analysisType;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(String fileLink) {
    }
}
