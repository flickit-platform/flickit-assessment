package org.flickit.assessment.kit.application.port.in.answerrange;

import io.jsonwebtoken.lang.Strings;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.AnswerOptionTranslation;

import java.util.Map;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_KIT_LANGUAGE_NOT_VALID;
import static org.flickit.assessment.common.validation.EnumValidateUtils.validateAndConvert;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface CreateAnswerRangeOptionUseCase {

    Result createAnswerRangeOption(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = CREATE_ANSWER_RANGE_OPTION_KIT_VERSION_ID_NOT_NULL)
        Long kitVersionId;

        @NotNull(message = CREATE_ANSWER_RANGE_OPTION_ANSWER_RANGE_ID_NOT_NULL)
        Long answerRangeId;

        @NotNull(message = CREATE_ANSWER_RANGE_OPTION_INDEX_NOT_NULL)
        Integer index;

        @NotBlank(message = CREATE_ANSWER_RANGE_OPTION_TITLE_NOT_BLANK)
        @Size(max = 100, message = CREATE_ANSWER_RANGE_OPTION_TITLE_SIZE_MAX)
        String title;

        @NotNull(message = CREATE_ANSWER_RANGE_OPTION_VALUE_NOT_NULL)
        Double value;

        @Nullable
        @Valid
        Map<KitLanguage, AnswerOptionTranslation> translations;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long kitVersionId,
                     Long answerRangeId,
                     Integer index,
                     String title,
                     Double value,
                     Map<String, AnswerOptionTranslation> translations,
                     UUID currentUserId) {
            this.kitVersionId = kitVersionId;
            this.answerRangeId = answerRangeId;
            this.index = index;
            this.title = Strings.trimWhitespace(title);
            this.value = value;
            this.translations = validateAndConvert(translations, KitLanguage.class, COMMON_KIT_LANGUAGE_NOT_VALID);
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(long id) {
    }
}
