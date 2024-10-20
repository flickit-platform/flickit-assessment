package org.flickit.assessment.kit.application.port.in.question;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_QUESTION_DETAIL_KIT_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_QUESTION_DETAIL_QUESTION_ID_NOT_NULL;

public interface GetKitQuestionDetailUseCase {

    Result getKitQuestionDetail(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_KIT_QUESTION_DETAIL_KIT_ID_NOT_NULL)
        Long kitId;

        @NotNull(message = GET_KIT_QUESTION_DETAIL_QUESTION_ID_NOT_NULL)
        Long questionId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(Long kitId, Long questionId, UUID currentUserId) {
            this.kitId = kitId;
            this.questionId = questionId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(String hint, List<Option> options, List<Impact> attributeImpacts) {
    }

    record Option(int index, String title) {
    }

    record Impact(long id, String title, List<AffectedLevel> affectedLevels) {
    }

    record AffectedLevel(MaturityLevel maturityLevel, int weight, List<OptionValue> optionValues) {
        public record MaturityLevel(long id, int index, String title) {
        }

        public record OptionValue(long id, int index, double value) {
        }
    }
}
