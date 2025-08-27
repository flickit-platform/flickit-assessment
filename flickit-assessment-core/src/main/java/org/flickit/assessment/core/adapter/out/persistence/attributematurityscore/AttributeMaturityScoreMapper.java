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
        var questionnaire = view.getQuestionnaire();
        var question = view.getQuestion();
        var option = view.getOption();
        var answer = view.getAnswer();
        var questionnaireTranslation = QuestionnaireMapper.getTranslation(questionnaire, translationLanguage);
        var questionTranslation = QuestionMapper.getTranslation(question, translationLanguage);
        var answerOptionTranslation = option == null ? null : AnswerOptionMapper.getTranslation(option, translationLanguage);

        return new LoadAttributeScoreDetailPort.Result(questionnaire.getId(),
            questionnaireTranslation.titleOrDefault(questionnaire.getTitle()),
            question.getId(),
            question.getIndex(),
            questionTranslation.titleOrDefault(question.getTitle()),
            view.getQuestionImpact().getWeight(),
            option == null ? null : option.getIndex(),
            option == null ? null : answerOptionTranslation.titleOrDefault(option.getTitle()),
            answer == null ? null : answer.getIsNotApplicable(),
            view.getGainedScore(),
            view.getMissedScore(),
            answer != null && answer.getConfidenceLevelId() != null ? answer.getConfidenceLevelId() : null,
            view.getEvidenceCount());
    }
}
