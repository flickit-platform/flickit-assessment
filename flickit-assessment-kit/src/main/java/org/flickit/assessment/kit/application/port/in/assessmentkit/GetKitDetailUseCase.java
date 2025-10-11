package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.AnswerOptionTranslation;
import org.flickit.assessment.common.application.domain.kit.translation.AnswerRangeTranslation;
import org.flickit.assessment.kit.application.domain.AnswerOption;
import org.flickit.assessment.common.application.domain.kit.translation.*;
import org.flickit.assessment.kit.application.domain.Attribute;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_DETAIL_KIT_ID_NOT_NULL;

public interface GetKitDetailUseCase {

    Result getKitDetail(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_KIT_DETAIL_KIT_ID_NOT_NULL)
        Long kitId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(Long kitId, UUID currentUserId) {
            this.kitId = kitId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(
        List<KitDetailMaturityLevel> maturityLevels,
        List<KitDetailSubject> subjects,
        List<KitDetailQuestionnaire> questionnaires,
        List<KitDetailMeasure> measures,
        List<KitDetailAnswerRange> answerRanges) {
    }

    record KitDetailMaturityLevel(long id,
                                  String title,
                                  int index,
                                  String description,
                                  List<Competences> competences,
                                  Map<KitLanguage, MaturityLevelTranslation> translations) {
    }

    record Competences(String title, int value, long maturityLevelId,
                       Map<KitLanguage, MaturityLevelTranslation> translations) {
    }

    record KitDetailSubject(long id, String title, int index, List<KitDetailAttribute> attributes,
                            Map<KitLanguage, SubjectTranslation> translations) {
    }

    record KitDetailAttribute(long id, String title, int index, Map<KitLanguage, AttributeTranslation> translations) {

        public static KitDetailAttribute of(Attribute attribute) {
            return new KitDetailAttribute(attribute.getId(),
                attribute.getTitle(),
                attribute.getIndex(),
                attribute.getTranslations());
        }
    }

    record KitDetailQuestionnaire(long id, String title, int index,
                                  Map<KitLanguage, QuestionnaireTranslation> translations) {
    }

    record KitDetailMeasure(long id, String title, int index, Map<KitLanguage, MeasureTranslation> translations) {
    }

    record KitDetailAnswerRange(long id, String title, List<KitDetailAnswerOption> answerOptions,
                                Map<KitLanguage, AnswerRangeTranslation> translations) {
    }

    record KitDetailAnswerOption(int index, String title, double value,
                                 Map<KitLanguage, AnswerOptionTranslation> translations) {

        public static KitDetailAnswerOption of(AnswerOption answerOption) {
            return new KitDetailAnswerOption(answerOption.getIndex(), answerOption.getTitle(), answerOption.getValue(), answerOption.getTranslations());
        }
    }
}
