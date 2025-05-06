package org.flickit.assessment.core.application.port.in.assessmentreport;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_PUBLIC_REPORT_LINK_HASH_NOT_NULL;

public interface GetAssessmentPublicReportUseCase {

    Result getAssessmentPublicReport(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_ASSESSMENT_PUBLIC_REPORT_LINK_HASH_NOT_NULL)
        UUID linkHash;

        UUID currentUserId;

        @Builder
        public Param(UUID linkHash, UUID currentUserId) {
            this.linkHash = linkHash;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(Assessment assessment,
                  List<Subject> subjects,
                  Advice advice,
                  AssessmentProcess assessmentProcess,
                  Permissions permissions,
                  Language lang,
                  Role role) {
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
                     int weight,
                     double confidenceValue,
                     MaturityLevel maturityLevel,
                     List<AttributeMeasure> attributeMeasures) {
    }

    record AttributeMeasure(String title,
                            Double impactPercentage,
                            Double maxPossibleScore,
                            Double gainedScore,
                            Double missedScore,
                            Double gainedScorePercentage,
                            Double missedScorePercentage) {
    }

    record Advice(String narration, List<AdviceItem> adviceItems) {
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

    record Permissions(boolean canViewDashboard) {
    }

    record AdviceItem(UUID id,
                      String title,
                      String description,
                      Level cost,
                      Level priority,
                      Level impact) {

        public record Level(String code, String title) {
        }
    }

    record Language(String code) {
    }

    record Role(int id, String title) {
        public static Role of(AssessmentUserRole role) {
            return new Role(role.getId(), role.getTitle());
        }
    }
}
