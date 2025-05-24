package org.flickit.assessment.core.application.port.in.assessment;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentMode;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.domain.Space;
import org.flickit.assessment.core.application.domain.User;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_ASSESSMENT_ID_NOT_NULL;

public interface GetAssessmentUseCase {

    /**
     * @throws ResourceNotFoundException if no assessment found by the given id
     */
    Result getAssessment(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_ASSESSMENT_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(UUID assessmentId, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(UUID id,
                  String title,
                  String shortTitle,
                  Space space,
                  Long kitCustomId,
                  AssessmentKit kit,
                  Mode mode,
                  LocalDateTime creationTime,
                  LocalDateTime lastModificationTime,
                  User createdBy,
                  MaturityLevel maturityLevel,
                  boolean isCalculateValid,
                  Language language,
                  boolean manageable,
                  boolean viewable) {

        public record AssessmentKit(long id, String title) {
        }

        public record Language(String code, String title) {
            public static Language of(KitLanguage language) {
                return new Language(language.getCode(), language.getTitle());
            }
        }

        public record Mode(String code, String title) {
            public static Mode of(AssessmentMode mode) {
                return new Mode(mode.getCode(), mode.getTitle());
            }
        }
    }
}
