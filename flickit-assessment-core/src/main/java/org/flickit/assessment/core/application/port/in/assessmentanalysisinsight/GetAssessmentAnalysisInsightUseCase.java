package org.flickit.assessment.core.application.port.in.assessmentanalysisinsight;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.validation.EnumValue;
import org.flickit.assessment.core.application.domain.AssessmentAnalysisInsight;
import org.flickit.assessment.core.application.domain.AssessmentAnalysisType;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

public interface GetAssessmentAnalysisInsightUseCase {

    Result getAssessmentAnalysisInsight(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_ASSESSMENT_ANALYSIS_INSIGHT_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotBlank(message = GET_ASSESSMENT_ANALYSIS_INSIGHT_TYPE_NOT_BLANK)
        @EnumValue(enumClass = AssessmentAnalysisType.class, message = GET_ASSESSMENT_ANALYSIS_INSIGHT_TYPE_INVALID)
        String type;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(UUID assessmentId, String type, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.type = type;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(AnalysisInsight aiAnalysis, AnalysisInsight assessorAnalysis, boolean editable) {

        public record AnalysisInsight(Analysis analysis, LocalDateTime analysisTime) {

            interface Analysis {}

            public record Message(String msg) implements Analysis {}

            public record AiInsight(AssessmentAnalysisInsight insight) implements Analysis {}

            public record AssessorInsight(AssessmentAnalysisInsight insight) implements Analysis {}
        }
    }
}
