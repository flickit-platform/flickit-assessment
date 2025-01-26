package org.flickit.assessment.core.application.port.in.assessmentreport;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_REPORT_ASSESSMENT_ID_NOT_NULL;

public interface GetAssessmentReportUseCase {

    Result getAssessmentReport(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_ASSESSMENT_REPORT_ASSESSMENT_ID_NOT_NULL)
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
                  List<Subject> subjects,
                  AssessmentProcess assessmentProcess) {
    }

    record Assessment(String title,
                      String intro,
                      String overallInsight,
                      String prosAndCons,
                      AssessmentKit assessmentKit,
                      MaturityLevel maturityLevel,
                      double confidenceValue,
                      LocalDateTime creationTime) {
    }

    record AssessmentKit(long id,
                         String title,
                         int maturityLevelCount,
                         int questionsCount,
                         int questionnairesCount,
                         int attributesCount,
                         List<Questionnaire> questionnaires,
                         List<MaturityLevel> maturityLevels) {
    }

    record MaturityLevel(long id,
                         String title,
                         int index,
                         int value,
                         String description) {
    }

    record Subject(long id,
                   String title,
                   int index,
                   String insight,
                   double confidenceValue,
                   MaturityLevel maturityLevel,
                   List<Attribute> attributes) {
    }

    record Attribute(long id,
                     String title,
                     String description,
                     String insight,
                     int index,
                     double confidenceValue,
                     MaturityLevel maturityLevel) {
    }

    record Questionnaire(long id,
                         String title,
                         String description,
                         int index,
                         int questionCount) {
    }

    record AssessmentProcess(String steps,
                             String participant) {
    }
}
