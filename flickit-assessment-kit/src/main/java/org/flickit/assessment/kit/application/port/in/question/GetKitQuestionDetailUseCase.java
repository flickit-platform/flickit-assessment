package org.flickit.assessment.kit.application.port.in.question;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.AnswerOptionTranslation;
import org.flickit.assessment.common.application.domain.kit.translation.QuestionTranslation;
import org.flickit.assessment.kit.application.domain.AnswerRange;
import org.flickit.assessment.kit.application.domain.Measure;

import java.util.List;
import java.util.Map;
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

    record Result(String hint,
                  List<Option> options,
                  List<Impact> attributeImpacts,
                  QuestionDetailAnswerRange answerRange,
                  QuestionDetailMeasure measure,
                  Map<KitLanguage, QuestionTranslation> translations) {
    }

    record Option(int index, String title, double value, Map<KitLanguage, AnswerOptionTranslation> translations) {
    }

    record Impact(long id, String title, List<AffectedLevel> affectedLevels) {
    }

    record AffectedLevel(MaturityLevel maturityLevel, int weight) {
        public record MaturityLevel(long id, int index, String title) {
        }
    }

    record QuestionDetailAnswerRange(long id, String title) {

        public static QuestionDetailAnswerRange of(AnswerRange answerRange) {
            return new QuestionDetailAnswerRange(answerRange.getId(), answerRange.getTitle());
        }
    }

    record QuestionDetailMeasure(long id, String title) {

        public static QuestionDetailMeasure of(Measure measure) {
            return new QuestionDetailMeasure(measure.getId(), measure.getTitle());
        }
    }
}
