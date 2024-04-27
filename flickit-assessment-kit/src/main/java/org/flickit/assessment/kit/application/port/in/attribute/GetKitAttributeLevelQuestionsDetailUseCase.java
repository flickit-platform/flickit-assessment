package org.flickit.assessment.kit.application.port.in.attribute;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface GetKitAttributeLevelQuestionsDetailUseCase {

    Result getKitAttributeLevelQuestionsDetail(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_ATTRIBUTE_LEVEL_QUESTIONS_KIT_ID_NOT_NULL)
        Long kitId;

        @NotNull(message = GET_ATTRIBUTE_LEVEL_QUESTIONS_ATTRIBUTE_ID_NOT_NULL)
        Long attributeId;

        @NotNull(message = GET_ATTRIBUTE_LEVEL_QUESTIONS_MATURITY_LEVEL_ID_NOT_NULL)
        Long maturityLevelId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(Long kitId, Long attributeId, Long maturityLevelId, UUID currentUserId) {
            this.kitId = kitId;
            this.attributeId = attributeId;
            this.maturityLevelId = maturityLevelId;
            this.currentUserId = currentUserId;
            validateSelf();
        }
    }

    record Result(int questionsCount, List<Question> questions) {

        public record Question(
            int index,
            String title,
            boolean mayNotBeApplicable,
            boolean advisable,
            int weight,
            String questionnaire,
            List<AnswerOption> answerOptions) {

            public record AnswerOption(int index, String title, double value) {
            }
        }
    }
}
