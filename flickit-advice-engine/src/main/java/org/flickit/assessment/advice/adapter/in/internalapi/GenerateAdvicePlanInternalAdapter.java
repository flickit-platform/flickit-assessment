package org.flickit.assessment.advice.adapter.in.internalapi;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.domain.AttributeLevelTarget;
import org.flickit.assessment.advice.application.domain.advice.QuestionRecommendation;
import org.flickit.assessment.advice.application.port.in.advice.GenerateAdvicePlanInternalUseCase;
import org.flickit.assessment.common.application.domain.advice.*;
import org.flickit.assessment.common.application.module.adviceengine.GenerateAdvicePlanInternalApi;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GenerateAdvicePlanInternalAdapter implements GenerateAdvicePlanInternalApi {

    private final GenerateAdvicePlanInternalUseCase useCase;

    @Override
    public Result generate(Param param) {
        var useCaseParam = new GenerateAdvicePlanInternalUseCase.Param(param.assessmentId(), toTargets(param.attributeLevelTargets()));
        return new Result(toResult(useCase.generate(useCaseParam).adviceItems()));
    }

    private List<AttributeLevelTarget> toTargets(List<org.flickit.assessment.common.application.domain.advice.AttributeLevelTarget> targets) {
        return targets.stream()
            .map(t -> new AttributeLevelTarget(t.getAttributeId(), t.getMaturityLevelId()))
            .toList();
    }

    private List<AdvicePlanItem> toResult(List<QuestionRecommendation> result) {
        return result.stream()
            .map(r -> {
                AdviceQuestion question = toCommonAdviceQuestion(r.question());
                AdviceOption answeredOption = toCommonAdviceOption(r.answeredOption());
                AdviceOption recommendedOption = toCommonAdviceOption(r.recommendedOption());
                List<AdviceAttribute> attributes = toCommonAdviceAttributes(r.attributes());
                AdviceQuestionnaire questionnaire = toCommonAdviceQuestionnaire(r.questionnaire());
                return new AdvicePlanItem(question, answeredOption, recommendedOption, attributes, questionnaire);
            })
            .toList();
    }

    private AdviceQuestion toCommonAdviceQuestion(org.flickit.assessment.advice.application.domain.advice.AdviceQuestion adviceQuestion) {
        return new AdviceQuestion(adviceQuestion.id(), adviceQuestion.title(), adviceQuestion.index());
    }

    private AdviceOption toCommonAdviceOption(org.flickit.assessment.advice.application.domain.advice.AdviceOption adviceOption) {
        return new AdviceOption(adviceOption.index(), adviceOption.title());
    }

    private List<AdviceAttribute> toCommonAdviceAttributes(List<org.flickit.assessment.advice.application.domain.advice.AdviceAttribute> attributes) {
        return attributes.stream()
            .map(a -> new AdviceAttribute(a.id(), a.title()))
            .toList();
    }

    private AdviceQuestionnaire toCommonAdviceQuestionnaire(org.flickit.assessment.advice.application.domain.advice.AdviceQuestionnaire adviceQuestionnaire) {
        return new AdviceQuestionnaire(adviceQuestionnaire.id(), adviceQuestionnaire.title());
    }

}
