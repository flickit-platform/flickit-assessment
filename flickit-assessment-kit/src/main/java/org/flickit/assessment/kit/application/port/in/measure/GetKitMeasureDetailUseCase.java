package org.flickit.assessment.kit.application.port.in.measure;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.MeasureTranslation;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface GetKitMeasureDetailUseCase {

    Result getKitMeasureDetail(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_KIT_MEASURE_KIT_ID_NOT_NULL)
        Long kitId;

        @NotNull(message = GET_KIT_MEASURE_MEASURE_ID_NOT_NULL)
        Long measureId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long kitId, Long measureId, UUID currentUserId) {
            this.kitId = kitId;
            this.measureId = measureId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(String title, String description, int questionsCount, List<MeasureDetailQuestion> questions,
                  Map<KitLanguage, MeasureTranslation> translations) {
    }

    record MeasureDetailQuestion(String title,
                                 MeasureDetailAnswerRange answerRange,
                                 MeasureDetailQuestionnaire questionnaire,
                                 List<Option> options) {
    }

    record MeasureDetailAnswerRange(long id, String title) {
    }

    record MeasureDetailQuestionnaire(long id, String title) {
    }

    record Option(long id, String title, int index, double value) {
    }
}
