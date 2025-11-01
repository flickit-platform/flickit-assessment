package org.flickit.assessment.core.application.service.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.port.in.questionnaire.GetAssessmentQuestionnaireQuestionListUseCase;
import org.flickit.assessment.core.application.port.out.answer.LoadQuestionsAnswerListPort;
import org.flickit.assessment.core.application.port.out.answerhistory.LoadAnswerHistoryPort;
import org.flickit.assessment.core.application.port.out.evidence.CountEvidencesPort;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionnaireQuestionListPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_QUESTIONNAIRE_QUESTIONS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentQuestionnaireQuestionListService implements GetAssessmentQuestionnaireQuestionListUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final ValidateAssessmentResultPort validateAssessmentResultPort;
    private final LoadQuestionnaireQuestionListPort loadQuestionnaireQuestionListPort;
    private final LoadQuestionsAnswerListPort loadQuestionsAnswerListPort;
    private final CountEvidencesPort countEvidencesPort;
    private final LoadAnswerHistoryPort loadAnswerHistoryPort;

    @Override
    public PaginatedResponse<Result> getQuestionnaireQuestionList(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_QUESTIONNAIRE_QUESTIONS))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        validateAssessmentResultPort.validate(param.getAssessmentId());

        var pageResult = loadQuestionnaireQuestionListPort.loadByQuestionnaireId(param.getQuestionnaireId(),
            param.getAssessmentId(),
            param.getSize(),
            param.getPage());

        List<Long> questionIds = pageResult.getItems().stream()
            .map(Question::getId)
            .toList();

        var questionIdToAnswerMap = loadQuestionsAnswerListPort.loadByQuestionIds(param.getAssessmentId(), questionIds).stream()
            .collect(toMap(Answer::getQuestionId, Function.identity()));

        var questionIdToAnswerHistoriesCountMap = loadAnswerHistoryPort.countAnswerHistories(param.getAssessmentId(), questionIds);
        var questionIdToEvidencesCountMap = countEvidencesPort.countQuestionnaireQuestionsEvidences(param.getAssessmentId(), param.getQuestionnaireId());
        var questionIdToUnresolvedCommentsCountMap = countEvidencesPort.countUnresolvedComments(param.getAssessmentId(), param.getQuestionnaireId());
        var items = pageResult.getItems().stream()
            .map((Question q) -> mapToResult(q,
                questionIdToAnswerMap.get(q.getId()),
                questionIdToEvidencesCountMap.getOrDefault(q.getId(), 0),
                questionIdToUnresolvedCommentsCountMap.getOrDefault(q.getId(), 0),
                questionIdToAnswerHistoriesCountMap.getOrDefault(q.getId(), 0)))
            .toList();

        return new PaginatedResponse<>(
            items,
            pageResult.getPage(),
            pageResult.getSize(),
            pageResult.getSort(),
            pageResult.getOrder(),
            pageResult.getTotal());
    }

    private Result mapToResult(Question question, Answer answer, int evidencesCount, int unresolvedCommentsCount, int answerHistoryCount) {
        QuestionAnswer answerDto = null;
        if (answer != null) {
            Option answerOption = null;
            if (!Boolean.TRUE.equals(answer.getIsNotApplicable()) && answer.getSelectedOption() != null) {
                answerOption = question.getOptions().stream()
                    .filter(x -> Objects.equals(x.getId(), answer.getSelectedOption().getId()))
                    .map(this::mapToOption)
                    .findAny()
                    .orElse(null);
            }
            ConfidenceLevel confidenceLevel = null;
            Boolean approved = null;
            if (answerOption != null || Boolean.TRUE.equals(answer.getIsNotApplicable())) {
                confidenceLevel = ConfidenceLevel.valueOfById(answer.getConfidenceLevelId());
                approved = AnswerStatus.APPROVED.equals(answer.getAnswerStatus());
            }
            answerDto = new QuestionAnswer(answerOption, confidenceLevel, answer.getIsNotApplicable(), approved);
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
            answerDto,
            new Issues(
                !hasAnswer(answer),
                hasAnswer(answer) && answer.getConfidenceLevelId() < ConfidenceLevel.SOMEWHAT_UNSURE.getId(),
                hasAnswer(answer) && evidencesCount == 0,
                unresolvedCommentsCount,
                answerDto != null && answerDto.approved() != null && !answerDto.approved()),
            new Counts(evidencesCount,
                unresolvedCommentsCount,
                answerHistoryCount));
    }

    private boolean hasAnswer(Answer answer) {
        if (answer == null)
            return false;

        return answer.getSelectedOption() != null || Boolean.TRUE.equals(answer.getIsNotApplicable());
    }

    private Option mapToOption(AnswerOption option) {
        return new Option(option.getId(), option.getIndex(), option.getTitle());
    }
}
