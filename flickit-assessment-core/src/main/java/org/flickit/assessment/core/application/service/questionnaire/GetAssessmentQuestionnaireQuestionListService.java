package org.flickit.assessment.core.application.service.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.Answer;
import org.flickit.assessment.core.application.domain.ConfidenceLevel;
import org.flickit.assessment.core.application.domain.Question;
import org.flickit.assessment.core.application.port.in.questionnaire.GetAssessmentQuestionnaireQuestionListUseCase;
import org.flickit.assessment.core.application.port.out.answer.LoadAssessmentQuestionnaireAnswerListPort;
import org.flickit.assessment.core.application.port.out.assessment.CheckUserAssessmentAccessPort;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionnaireQuestionListPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.application.domain.ConfidenceLevel.valueOfById;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentQuestionnaireQuestionListService implements GetAssessmentQuestionnaireQuestionListUseCase {

    private final CheckUserAssessmentAccessPort checkUserAssessmentAccessPort;
    private final LoadQuestionnaireQuestionListPort loadQuestionnaireQuestionListPort;
    private final LoadAssessmentQuestionnaireAnswerListPort loadAssessmentQuestionnaireAnswerListPort;

    @Override
    public PaginatedResponse<Result> getAssessmentQuestionnaireQuestionList(Param param) {
        if (!checkUserAssessmentAccessPort.hasAccess(param.getAssessmentId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        var pageResult = loadQuestionnaireQuestionListPort.loadByQuestionnaireId(param.getQuestionnaireId(),
            param.getSize(),
            param.getPage());

        Map<Long, Answer> questionIdToAnswerMap = loadAssessmentQuestionnaireAnswerListPort.loadQuestionnaireAnswers(param.getAssessmentId(), param.getQuestionnaireId(),
                param.getSize(),
                param.getPage())
            .stream()
            .collect(toMap(Answer::getQuestionId, Function.identity()));

        var items = pageResult.getItems().stream()
            .map((Question q) -> mapToResult(q, questionIdToAnswerMap.get(q.getId()))).toList();

        return new PaginatedResponse<>(
            items,
            pageResult.getPage(),
            pageResult.getSize(),
            pageResult.getSort(),
            pageResult.getOrder(),
            pageResult.getTotal());
    }

    private Result mapToResult(Question question, Answer answer) {
        ConfidenceLevel confidenceLevel = null;
        AnswerOption selectedOption = null;
        Boolean isNotApplicable = null;
        if (answer != null) {
            confidenceLevel = valueOfById(answer.getConfidenceLevelId());
            var answerOption = question.getOptions().stream()
                .filter(x -> Objects.equals(x.getId(), answer.getSelectedOption().getId()))
                .findAny()
                .orElse(null);
            if (answerOption != null)
                selectedOption = mapToAnswer(answerOption);
            isNotApplicable = answer.getIsNotApplicable();
        }
        return new Result(
            question.getId(),
            question.getTitle(),
            question.getIndex(),
            question.getHint(),
            question.getMayNotBeApplicable(),
            question.getOptions().stream()
                .map(this::mapToOptionListItem)
                .toList(),
            selectedOption,
            confidenceLevel,
            isNotApplicable);
    }

    private OptionListItem mapToOptionListItem(org.flickit.assessment.core.application.domain.AnswerOption ao) {
        return new OptionListItem(ao.getId(), ao.getTitle(), ao.getIndex());
    }

    private AnswerOption mapToAnswer(org.flickit.assessment.core.application.domain.AnswerOption selectedOption) {
        return new AnswerOption(selectedOption.getId(), selectedOption.getTitle(), selectedOption.getIndex());
    }
}
