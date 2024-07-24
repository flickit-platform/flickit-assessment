package org.flickit.assessment.core.application.service.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.Answer;
import org.flickit.assessment.core.application.domain.AnswerOption;
import org.flickit.assessment.core.application.domain.ConfidenceLevel;
import org.flickit.assessment.core.application.domain.Question;
import org.flickit.assessment.core.application.port.in.evidence.GetEvidenceUseCase;
import org.flickit.assessment.core.application.port.out.answer.LoadAnswerPort;
import org.flickit.assessment.core.application.port.out.answeroption.LoadAnswerOptionsByQuestionPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencePort;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionPort;
import org.flickit.assessment.core.application.port.out.user.LoadUserPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_EVIDENCE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_EVIDENCE_ASSESSMENT_RESULT_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetEvidenceService implements GetEvidenceUseCase {

    private final LoadEvidencePort loadEvidencePort;
    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadUserPort loadUserPort;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadAnswerOptionsByQuestionPort loadAnswerOptionsByQuestionPort;
    private final LoadQuestionPort loadQuestionPort;
    private final LoadAnswerPort loadAnswerPort;

    @Override
    public Result getEvidence(Param param) {
        var evidence = loadEvidencePort.loadNotDeletedEvidence(param.getId());
        if (!assessmentAccessChecker.isAuthorized(evidence.getAssessmentId(), param.getCurrentUserId(), VIEW_EVIDENCE))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(evidence.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_EVIDENCE_ASSESSMENT_RESULT_NOT_FOUND));
        var kitVersionId = assessmentResult.getKitVersionId();

        var answerOptions = loadAnswerOptionsByQuestionPort.loadByQuestionId(evidence.getQuestionId(), kitVersionId);
        Question question = loadQuestionPort.loadByIdAndKitVersionId(evidence.getQuestionId(), kitVersionId);
        question.setOptions(answerOptions);
        QuestionAnswer answer = buildAnswer(assessmentResult.getId(), question.getId(), question.getOptions());

        var user = loadUserPort.loadById(evidence.getCreatedById()).orElse(null);

        return new Result(evidence, question, answer, user);
    }

    private QuestionAnswer buildAnswer(UUID assessmentResultId, long questionId, List<AnswerOption> questionOptions) {
        var answer = loadAnswerPort.load(assessmentResultId, questionId);

        return answer.map(loadedAnswer -> {
            Option answerOption = mapToAnswerOption(loadedAnswer, questionOptions);
            ConfidenceLevel confidenceLevel = mapToConfidenceLevel(loadedAnswer, answerOption);
            return new QuestionAnswer(answerOption, confidenceLevel, loadedAnswer.getIsNotApplicable());
        }).orElse(null);
    }

    private Option mapToAnswerOption(Answer loadedAnswer, List<AnswerOption> questionOptions) {
        if (Boolean.TRUE.equals(loadedAnswer.getIsNotApplicable()) || loadedAnswer.getSelectedOption() == null)
            return null;

        return questionOptions.stream()
            .filter(option -> Objects.equals(option.getId(), loadedAnswer.getSelectedOption().getId()))
            .map(this::mapToOption)
            .findAny()
            .orElse(null);
    }

    private Option mapToOption(AnswerOption option) {
        return new Option(option.getId(), option.getIndex(), option.getTitle());
    }

    private ConfidenceLevel mapToConfidenceLevel(Answer loadedAnswer, Option answerOption) {
        if (answerOption != null || Boolean.TRUE.equals(loadedAnswer.getIsNotApplicable()))
            return ConfidenceLevel.valueOfById(loadedAnswer.getConfidenceLevelId());

        return null;
    }
}
