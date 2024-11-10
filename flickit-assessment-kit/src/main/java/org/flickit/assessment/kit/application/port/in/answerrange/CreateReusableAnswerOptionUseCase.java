package org.flickit.assessment.kit.application.port.in.answerrange;

import io.jsonwebtoken.lang.Strings;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface CreateReusableAnswerOptionUseCase {

    Result createReusableAnswerOption(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = CREATE_REUSABLE_ANSWER_OPTION_KIT_VERSION_ID_NOT_NULL)
        Long kitVersionId;

        @NotNull(message = CREATE_REUSABLE_ANSWER_OPTION_ANSWER_RANGE_ID_NOT_NULL)
        Long answerRangeId;

        @NotNull(message = CREATE_REUSABLE_ANSWER_OPTION_INDEX_NOT_NULL)
        Integer index;

        @NotBlank(message = CREATE_REUSABLE_ANSWER_OPTION_TITLE_NOT_BLANK)
        @Size(max = 100, message = CREATE_REUSABLE_ANSWER_OPTION_TITLE_SIZE_MAX)
        String title;

        @NotNull(message = CREATE_REUSABLE_ANSWER_OPTION_VALUE_NOT_NULL)
        Double value;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long kitVersionId, Long answerRangeId, Integer index, String title, Double value, UUID currentUserId) {
            this.kitVersionId = kitVersionId;
            this.answerRangeId = answerRangeId;
            this.index = index;
            this.title = Strings.trimWhitespace(title);
            this.value = value;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(long id) {
    }
}
