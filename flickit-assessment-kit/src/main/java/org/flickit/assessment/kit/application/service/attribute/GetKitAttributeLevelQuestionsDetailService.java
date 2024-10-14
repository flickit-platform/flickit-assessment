package org.flickit.assessment.kit.application.service.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.*;
import org.flickit.assessment.kit.application.port.in.attribute.GetKitAttributeLevelQuestionsDetailUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadActiveKitVersionIdPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.question.LoadAttributeLevelQuestionsPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitAttributeLevelQuestionsDetailService implements GetKitAttributeLevelQuestionsDetailUseCase {

    private final CheckExpertGroupAccessPort checkExpertGroupAccessPort;
    private final LoadKitExpertGroupPort loadKitExpertGroupPort;
    private final LoadAttributeLevelQuestionsPort loadAttributeLevelQuestionsPort;
    private final LoadActiveKitVersionIdPort loadActiveKitVersionIdPort;

    @Override
    public Result getKitAttributeLevelQuestionsDetail(Param param) {
        ExpertGroup expertGroup = loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId());
        if (!checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        var kitVersionId = loadActiveKitVersionIdPort.loadKitVersionId(param.getKitId());

        var result = loadAttributeLevelQuestionsPort.loadAttributeLevelQuestions(kitVersionId, param.getAttributeId(), param.getMaturityLevelId());

        List<Result.Question> questions = result.stream()
            .map(e -> mapToResultQuestion(e.question(), e.questionnaire()))
            .sorted(Comparator.comparing(Result.Question::questionnaire)
                .thenComparing(Result.Question::index))
            .toList();

        return new Result(questions.size(), questions);
    }

    private Result.Question mapToResultQuestion(Question question, Questionnaire questionnaire) {
        var impact = question.getImpacts().get(0);
        List<Result.Question.AnswerOption> options = mapToAnswerOptions(question, impact);
        return new Result.Question(
            question.getIndex(),
            question.getTitle(),
            question.getMayNotBeApplicable(),
            question.getAdvisable(),
            impact.getWeight(),
            questionnaire.getTitle(),
            options
        );
    }

    private List<Result.Question.AnswerOption> mapToAnswerOptions(Question question, QuestionImpact impact) {
        return question.getOptions().stream()
            .map(option -> mapToAnswerOption(option, impact))
            .toList();
    }

    private Result.Question.AnswerOption mapToAnswerOption(AnswerOption option, QuestionImpact impact) {
        double optionImpactValue = impact.getOptionImpacts().stream()
            .filter(i -> i.getOptionId() == option.getId())
            .map(AnswerOptionImpact::getValue)
            .findFirst()
            .orElseThrow();
        return new Result.Question.AnswerOption(
            option.getIndex(),
            option.getTitle(),
            optionImpactValue
        );
    }
}
