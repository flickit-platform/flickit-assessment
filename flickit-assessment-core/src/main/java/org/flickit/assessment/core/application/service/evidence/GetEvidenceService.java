package org.flickit.assessment.core.application.service.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.port.in.evidence.GetEvidenceUseCase;
import org.flickit.assessment.core.application.port.out.answer.LoadAnswerPort;
import org.flickit.assessment.core.application.port.out.answeroption.LoadAnswerOptionsByQuestionPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencePort;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionPort;
import org.flickit.assessment.core.application.port.out.user.LoadUserPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

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
        Answer answer = loadAnswerPort.load(assessmentResult.getId(), evidence.getQuestionId()).orElse(null);
        var user = loadUserPort.loadById(evidence.getCreatedById()).orElse(null);

        return mapToResult(evidence, answerOptions, question, answer, user);
    }

    Result mapToResult(Evidence evidence, List<AnswerOption> answerOptions, Question question, Answer answer, User user) {
        Result.ResultQuestion.ResultQuestionAnswer.ResultConfidenceLevel resultConfidenceLevel = null;
        Result.ResultQuestion.ResultQuestionAnswer.SelectedOption resultSelectedOption = null;
        Boolean isNotApplicable = null;

        if (answer != null) {
            ConfidenceLevel confidenceLevel = ConfidenceLevel.valueOfById(answer.getConfidenceLevelId());
            if (confidenceLevel != null) {
                resultConfidenceLevel = new Result.ResultQuestion.ResultQuestionAnswer.ResultConfidenceLevel(confidenceLevel.getId(), confidenceLevel.getTitle());
            }
            resultSelectedOption = new Result.ResultQuestion.ResultQuestionAnswer.SelectedOption(answer.getSelectedOption() != null ? answer.getSelectedOption().getId() : null);
            isNotApplicable = answer.getIsNotApplicable();
        }

        String evidenceType = (evidence.getType() != null) ? EvidenceType.values()[evidence.getType()].getTitle() : null;
        List<Result.ResultQuestion.QuestionOptions> questionAnswerOptions = (answerOptions != null) ?
            answerOptions.stream()
                .map(e -> new Result.ResultQuestion.QuestionOptions(e.getId(), e.getIndex(), e.getTitle()))
                .toList() : Collections.emptyList();

        return new Result(
            new Result.ResultEvidence(
                evidence.getId(),
                evidence.getDescription(),
                evidenceType,
                (user != null) ? user.getDisplayName() : null,
                evidence.getCreationTime(),
                evidence.getLastModificationTime()
            ),
            new Result.ResultQuestion(
                question.getId(),
                question.getTitle(),
                question.getIndex(),
                questionAnswerOptions,
                new Result.ResultQuestion.QuestionQuestionnaire(question.getQuestionnaire().getId(), question.getQuestionnaire().getTitle()),
                new Result.ResultQuestion.ResultQuestionAnswer(
                    resultSelectedOption,
                    resultConfidenceLevel,
                    isNotApplicable
                )
            )
        );
    }
}
