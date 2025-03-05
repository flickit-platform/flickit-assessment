package org.flickit.assessment.core.application.port.in.insight;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_INSIGHTS_ASSESSMENT_ID_NOT_NULL;

public interface GetAssessmentInsightsUseCase {

    Result getAssessmentInsights(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_ASSESSMENT_INSIGHTS_ASSESSMENT_ID_NOT_NULL)
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

    record Result(Assessment assessment,
                  List<SubjectModel> subjects,
                  Issues issues) {
    }

    record Assessment(UUID id,
                      String title,
                      MaturityLevelModel maturityLevel,
                      Double confidenceValue,
                      boolean isCalculateValid,
                      boolean isConfidenceValid,
                      InsightModel insight,
                      KitModel kit) {
    }

    record KitModel(int maturityLevelsCount) {
    }

    record SubjectModel(Long id,
                        String title,
                        String description,
                        Integer index,
                        Integer weight,
                        MaturityLevelModel maturityLevel,
                        Double confidenceValue,
                        InsightModel insight,
                        List<AttributeModel> attributes) {
    }

    record AttributeModel(Long id,
                          String title,
                          String description,
                          Integer index,
                          Integer weight,
                          MaturityLevelModel maturityLevel,
                          Double confidenceValue,
                          InsightModel insight) {
    }

    record Issues(int notGenerated,
                  int unapproved,
                  int expired) {
    }

    record MaturityLevelModel(long id,
                              String title,
                              int value,
                              int index) {
    }

    record InsightModel(InsightDetail defaultInsight,
                        InsightDetail assessorInsight,
                        boolean editable,
                        Boolean approved) {
        public record InsightDetail(String insight,
                                    LocalDateTime creationTime,
                                    boolean isValid) {
        }
    }
}
