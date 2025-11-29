package org.flickit.assessment.core.application.service.question;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.port.in.question.GetAssessmentQuestionUseCase;
import org.flickit.assessment.core.application.port.out.answer.LoadAnswerPort;
import org.flickit.assessment.core.application.port.out.answerhistory.LoadAnswerHistoryPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.evidence.CountEvidencesPort;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_DASHBOARD;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_QUESTIONNAIRE_QUESTIONS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_QUESTION_QUESTION_ID_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetAssessmentQuestionService implements GetAssessmentQuestionUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final ValidateAssessmentResultPort validateAssessmentResultPort;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadQuestionPort loadQuestionPort;
    private final LoadAnswerPort loadAnswerPort;
    private final CountEvidencesPort countEvidencesPort;
    private final LoadAnswerHistoryPort loadAnswerHistoryPort;

    @Override
    public Result getQuestion(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_QUESTIONNAIRE_QUESTIONS))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        validateAssessmentResultPort.validate(param.getAssessmentId());
        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));

        var question = loadQuestionPort.loadQuestionWithOptions(param.getQuestionId(), assessmentResult.getKitVersionId(), assessmentResult.getLanguage().getId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_ASSESSMENT_QUESTION_QUESTION_ID_NOT_FOUND));
        var answer = loadAnswerPort.load(assessmentResult.getId(), param.getQuestionId())
            .orElse(null);

        var answerHistoriesCount = 0;
        if (answer != null)
            answerHistoriesCount = loadAnswerHistoryPort.countQuestionAnswerHistories(assessmentResult.getId(), param.getQuestionId());
        var evidencesCount = countEvidencesPort.countQuestionEvidences(param.getAssessmentId(), param.getQuestionId());
        var commentsCount = countEvidencesPort.countQuestionUnresolvedComments(param.getAssessmentId(), param.getQuestionId());

        return buildResult(question,
            answer,
            evidencesCount,
            commentsCount,
            answerHistoriesCount,
            param.getAssessmentId(),
            param.getCurrentUserId());
    }

    private Result buildResult(Question question, Answer answer, int evidencesCount, int unresolvedCommentsCount, int answerHistoryCount, UUID assessmentId, UUID currentUserId) {
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

        Issues issues = null;
        if (assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_DASHBOARD))
            issues = new Issues(
                !hasAnswer(answer),
                hasAnswer(answer) && answer.getConfidenceLevelId() < ConfidenceLevel.SOMEWHAT_UNSURE.getId(),
                hasAnswer(answer) && evidencesCount == 0,
                unresolvedCommentsCount,
                answerDto != null && answerDto.approved() != null && !answerDto.approved());

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
            issues,
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

