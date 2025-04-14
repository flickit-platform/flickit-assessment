package org.flickit.assessment.kit.application.port.in.answerrange;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.AnswerRangeTranslation;

import java.util.Map;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_KIT_LANGUAGE_NOT_VALID;
import static org.flickit.assessment.common.validation.EnumValidateUtils.validateAndConvert;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface UpdateAnswerRangeUseCase {

    void updateAnswerRange(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPDATE_ANSWER_RANGE_KIT_VERSION_ID_NOT_NULL)
        Long kitVersionId;

        @NotNull(message = UPDATE_ANSWER_RANGE_ANSWER_RANGE_ID_NOT_NULL)
        Long answerRangeId;

        @Size(min = 3, message = UPDATE_ANSWER_RANGE_TITLE_SIZE_MIN)
        @Size(max = 100, message = UPDATE_ANSWER_RANGE_TITLE_SIZE_MAX)
        String title;

        @NotNull(message = UPDATE_ANSWER_RANGE_REUSABLE_NOT_NULL)
        Boolean reusable;

        @Valid
        Map<KitLanguage, AnswerRangeTranslation> translations;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long kitVersionId,
                     Long answerRangeId,
                     String title,
                     Boolean reusable,
                     Map<String, AnswerRangeTranslation> translations,
                     UUID currentUserId) {
            this.kitVersionId = kitVersionId;
            this.answerRangeId = answerRangeId;
            this.title = title != null && !title.isBlank() ? title.trim() : null;
            this.reusable = reusable;
            this.translations = validateAndConvert(translations, KitLanguage.class, COMMON_KIT_LANGUAGE_NOT_VALID);
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
