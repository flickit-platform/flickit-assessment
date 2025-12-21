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

    Result getQuestions(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_ATTRIBUTE_MEASURE_QUESTIONS_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = GET_ATTRIBUTE_MEASURE_QUESTIONS_ATTRIBUTE_ID_NOT_NULL)
        Long attributeId;

        @NotNull(message = GET_ATTRIBUTE_MEASURE_QUESTIONS_MEASURE_ID_NOT_NULL)
        Long measureId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(UUID assessmentId, Long attributeId, Long measureId, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.attributeId = attributeId;
            this.measureId = measureId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(List<MeasureQuestion> highScores, List<MeasureQuestion> lowScores) {
    }

    record MeasureQuestion(Question question, Answer answer, Questionnaire questionnaire) {

        public record Question(long id, int index, String title) {
        }

        public record Answer(Integer index,
                             String title,
                             Double gainedScore,
                             Double missedScore) {
        }

        public record Questionnaire(long id) {
        }
    }
}

