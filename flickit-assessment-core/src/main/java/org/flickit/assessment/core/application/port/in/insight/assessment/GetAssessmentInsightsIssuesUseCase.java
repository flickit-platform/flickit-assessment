package org.flickit.assessment.core.application.port.in.insight.assessment;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.core.application.domain.Attribute;
import org.flickit.assessment.core.application.domain.AttributeValue;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.domain.insight.Insight;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_INSIGHTS_ISSUES_ASSESSMENT_ID_NOT_NULL;

public interface GetAssessmentInsightsIssuesUseCase {

    Result getInsightsIssues(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_ASSESSMENT_INSIGHTS_ISSUES_ASSESSMENT_ID_NOT_NULL)
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

    record Result(int notGenerated,
                  int unapproved,
                  int expired) {
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

        public static AttributeModel of(Attribute attribute,
                                        AttributeValue attributeValue,
                                        Insight attributeInsight,
                                        boolean editable) {
            return new AttributeModel(attribute.getId(),
                attribute.getTitle(),
                attribute.getDescription(),
                attribute.getIndex(),
                attribute.getWeight(),
                MaturityLevelModel.of(attributeValue.getMaturityLevel()),
                attributeValue.getConfidenceValue(),
                InsightModel.of(attributeInsight, editable));
        }
    }

    record MaturityLevelModel(long id, String title, int value, int index) {

        public static MaturityLevelModel of(MaturityLevel maturityLevel) {
            return new MaturityLevelModel(maturityLevel.getId(),
                maturityLevel.getTitle(),
                maturityLevel.getValue(),
                maturityLevel.getIndex());
        }
    }

    record InsightModel(InsightModel.InsightDetail defaultInsight,
                        InsightModel.InsightDetail assessorInsight,
                        boolean editable,
                        Boolean approved) {
        public record InsightDetail(String insight,
                                    LocalDateTime creationTime,
                                    boolean isValid) {
            private static InsightModel.InsightDetail of(Insight.InsightDetail insightDetail) {
                return insightDetail != null
                    ? new InsightModel.InsightDetail(insightDetail.getInsight(), insightDetail.getCreationTime(), insightDetail.isValid())
                    : null;
            }
        }

        public static InsightModel of(Insight insight, boolean editable) {
            return insight != null
                ? new InsightModel(InsightModel.InsightDetail.of(insight.getDefaultInsight()),
                InsightModel.InsightDetail.of(insight.getAssessorInsight()),
                insight.isEditable(),
                insight.getApproved())
                : new InsightModel(null, null, editable, null);
        }
    }
}
