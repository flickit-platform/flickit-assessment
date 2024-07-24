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

import java.util.Objects;

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

        Answer answer = loadAnswerPort.load(assessmentResult.getId(), evidence.getQuestionId()).orElse(null);
        var user = loadUserPort.loadById(evidence.getCreatedById()).orElse(null);

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
            if (answerOption != null || Boolean.TRUE.equals(answer.getIsNotApplicable()))
                confidenceLevel = ConfidenceLevel.valueOfById(answer.getConfidenceLevelId());
            answerDto = new QuestionAnswer(answerOption, confidenceLevel, answer.getIsNotApplicable());
        }

        return new Result(evidence, question, answerDto, user);
    }

    private Option mapToOption(AnswerOption option) {
        return new Option(option.getId(), option.getIndex(), option.getTitle());
    }
}
