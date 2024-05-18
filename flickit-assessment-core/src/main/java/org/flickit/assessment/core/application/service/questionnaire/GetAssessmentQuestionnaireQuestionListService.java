package org.flickit.assessment.core.application.service.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.ConfidenceLevel;
import org.flickit.assessment.core.application.domain.Question;
import org.flickit.assessment.core.application.port.in.questionnaire.GetAssessmentQuestionnaireQuestionListUseCase;
import org.flickit.assessment.core.application.port.out.answer.LoadQuestionnaireAnswerListPort;
import org.flickit.assessment.core.application.port.out.assessment.CheckUserAssessmentAccessPort;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionnaireQuestionListPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentQuestionnaireQuestionListService implements GetAssessmentQuestionnaireQuestionListUseCase {

    private final CheckUserAssessmentAccessPort checkUserAssessmentAccessPort;
    private final LoadQuestionnaireQuestionListPort loadQuestionnaireQuestionListPort;
    private final LoadQuestionnaireAnswerListPort loadAssessmentQuestionnaireAnswerListPort;

    @Override
    public PaginatedResponse<Result> getAssessmentQuestionnaireQuestionList(Param param) {
        if (!checkUserAssessmentAccessPort.hasAccess(param.getAssessmentId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        var pageResult = loadQuestionnaireQuestionListPort.loadByQuestionnaireId(param.getQuestionnaireId(),
            param.getSize(),
            param.getPage());

        Map<Long, org.flickit.assessment.core.application.domain.Answer> questionIdToAnswerMap = loadAssessmentQuestionnaireAnswerListPort.loadQuestionnaireAnswers(param.getAssessmentId(), param.getQuestionnaireId(),
                param.getSize(),
                param.getPage())
            .getItems()
            .stream()
            .collect(toMap(org.flickit.assessment.core.application.domain.Answer::getQuestionId, Function.identity()));

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

    private Result mapToResult(Question question, org.flickit.assessment.core.application.domain.Answer answer) {
        QuestionAnswer answerDto = null;
        if (answer != null) {
            Option answerOption;
            if (Boolean.TRUE.equals(answer.getIsNotApplicable()))
                answerOption = null;
            else {
                answerOption = question.getOptions().stream()
                    .filter(x -> Objects.equals(x.getId(), answer.getSelectedOption().getId()))
                    .map(this::mapToOption)
                    .findAny()
                    .orElse(null);
            }
            answerDto = new QuestionAnswer(answerOption, ConfidenceLevel.valueOfById(answer.getConfidenceLevelId()), answer.getIsNotApplicable());
        }
        return new Result(
            question.getId(),
            question.getIndex(),
            question.getTitle(),
            question.getHint(),
            question.getMayNotBeApplicable(),
            question.getOptions().stream()
                .map(this::mapToOption)
                .toList(),
            answerDto);
    }

    private Option mapToOption(org.flickit.assessment.core.application.domain.AnswerOption ao) {
        return new Option(ao.getId(), ao.getTitle(), ao.getIndex());
    }
}
