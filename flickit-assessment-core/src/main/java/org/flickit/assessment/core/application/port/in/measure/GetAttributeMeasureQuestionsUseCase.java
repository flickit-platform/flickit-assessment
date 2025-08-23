package org.flickit.assessment.core.application.port.in.measure;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

public interface GetAttributeMeasureQuestionsUseCase {

    Result getAttributeMeasureQuestions(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_ATTRIBUTE_MEASURE_QUESTIONS_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = GET_ATTRIBUTE_MEASURE_QUESTIONS_ATTRIBUTE_ID_NOT_NULL)
        Long attributeId;

        @NotNull(message = GET_ATTRIBUTE_MEASURE_QUESTIONS_QUESTION_ID_NOT_NULL)
        Long questionId;

        @NotNull(message = GET_ATTRIBUTE_MEASURE_QUESTIONS_MEASURE_ID_NOT_NULL)
        Long measureId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(UUID assessmentId, Long attributeId, Long questionId, Long measureId, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.attributeId = attributeId;
            this.questionId = questionId;
            this.measureId = measureId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(List<Question> items) {
    }

    record Question(long id, int index, String title, int weight, Answer answer) {

        public record Answer(Integer index,
                             String title,
                             Boolean isNotApplicable,
                             Double gainedScore,
                             Double missedScore) {
        }
    }
}

