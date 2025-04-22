package org.flickit.assessment.core.adapter.out.persistence.attributematurityscore;

import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.core.adapter.out.persistence.kit.answeroption.AnswerOptionMapper;
import org.flickit.assessment.core.adapter.out.persistence.kit.question.QuestionMapper;
import org.flickit.assessment.core.adapter.out.persistence.kit.questionnaire.QuestionnaireMapper;
import org.flickit.assessment.core.application.domain.MaturityScore;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributeScoreDetailPort;
import org.flickit.assessment.data.jpa.core.attributematurityscore.AttributeMaturityScoreJpaEntity;
import org.flickit.assessment.data.jpa.kit.attribute.ImpactFullQuestionsView;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AttributeMaturityScoreMapper {

    public static AttributeMaturityScoreJpaEntity mapToJpaEntity(UUID attributeValueId, MaturityScore maturityScore) {
        return new AttributeMaturityScoreJpaEntity(
            attributeValueId,
            maturityScore.getMaturityLevelId(),
            maturityScore.getScore()
        );
    }

    public static LoadAttributeScoreDetailPort.Result mapToAttributeScoreDetail(ImpactFullQuestionsView view,
                                                                                @Nullable KitLanguage translationLanguage) {
        var questionnaireTranslation = QuestionnaireMapper.getTranslation(view.getQuestionnaireTranslation(), translationLanguage);
        var questionTranslation = QuestionMapper.getTranslation(view.getQuestionTranslation(), translationLanguage);
        var answerOptionTranslation = AnswerOptionMapper.getTranslation(view.getOptionTranslation(), translationLanguage);

        return new LoadAttributeScoreDetailPort.Result(view.getQuestionnaireId(),
            questionnaireTranslation.titleOrDefault(view.getQuestionnaireTitle()),
            view.getQuestionId(),
            view.getQuestionIndex(),
            questionTranslation.titleOrDefault(view.getQuestionTitle()),
            view.getQuestionImpact().getWeight(),
            view.getOptionIndex(),
            answerOptionTranslation.titleOrDefault(view.getOptionTitle()),
            view.getAnswer() == null ? null : view.getAnswer().getIsNotApplicable(),
            view.getGainedScore(),
            view.getMissedScore(),
            view.getAnswer() != null && view.getAnswer().getConfidenceLevelId() != null ? view.getAnswer().getConfidenceLevelId() : null,
            view.getEvidenceCount());
    }
}
