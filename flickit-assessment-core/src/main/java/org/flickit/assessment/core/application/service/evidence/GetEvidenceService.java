package org.flickit.assessment.core.application.service.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.adapter.in.rest.answeroption.LoadAnswerOptionsByQuestionPort;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.port.in.evidence.GetEvidenceUseCase;
import org.flickit.assessment.core.application.port.out.answer.LoadAnswerPort;
import org.flickit.assessment.core.application.port.out.assessment.CheckAssessmentSpaceMembershipPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencePort;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionPort;
import org.flickit.assessment.core.application.port.out.questionnaire.LoadQuestionnairePort;
import org.flickit.assessment.core.application.port.out.user.LoadUserPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetEvidenceService implements GetEvidenceUseCase {

    private final LoadEvidencePort loadEvidencePort;
    private final LoadUserPort loadUserPort;
    private final LoadAnswerPort loadAnswerPort;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadAnswerOptionsByQuestionPort loadAnswerOptionsByQuestionPort;
    private final CheckAssessmentSpaceMembershipPort checkAssessmentSpaceMembershipPort;
    private final LoadQuestionPort loadQuestionPort;
    private final LoadQuestionnairePort loadQuestionnairePort;

    @Override
    public Result getEvidence(Param param) {
        var evidencePortResult = loadEvidencePort.loadNotDeletedEvidence(param.getId());
        var user = loadUserPort.loadById(evidencePortResult.getCreatedById()).orElseThrow(); //TODO:Define

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(evidencePortResult.getAssessmentId()).orElseThrow(); //TODO

        var answerOptions = loadAnswerOptionsByQuestionPort.loadByQuestionId(evidencePortResult.getQuestionId(), assessmentResult.getKitVersionId());

        var question = loadQuestionPort.loadByIdAndKitVersionId(evidencePortResult.getQuestionId(), assessmentResult.getKitVersionId());

        var questionnaire = loadQuestionnairePort.loadByIdAndKitVersionId(question.questionnaireId(), assessmentResult.getKitVersionId()).orElseThrow();

        var answer = loadAnswerPort.load(assessmentResult.getId(), evidencePortResult.getQuestionId()).orElse(null);

        if (!checkAssessmentSpaceMembershipPort.isAssessmentSpaceMember(evidencePortResult.getAssessmentId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        return mapToResult(evidencePortResult, answerOptions, question, questionnaire, answer, user);
    }

    Result mapToResult(Evidence evidence, List<AnswerOption> answerOptions, LoadQuestionPort.Result question, LoadQuestionnairePort.Result questionnaire, Answer answer, User user) {
        var confidenceLevel = (answer != null) ? ConfidenceLevel.valueOfById(answer.getConfidenceLevelId()) : null;
        var resultConfidenceLevel = new Result.ResultQuestion.ResultQuestionAnswer.ResultConfidenceLevel(confidenceLevel.getId(), confidenceLevel.getTitle());
        var resultQuestionAnswer = new Result.ResultQuestion.ResultQuestionAnswer(
            new Result.ResultQuestion.ResultQuestionAnswer.SelectedOption(answer.getSelectedOption().getId()),
            resultConfidenceLevel,
            answer.getIsNotApplicable()
        );
        var evidenceType = (evidence.getType() != null) ? EvidenceType.values()[evidence.getType()].getTitle() : null;
        var questionAnswerOptions = answerOptions.stream().map(e -> new Result.ResultQuestion.QuestionOptions(e.getId(), e.getIndex(), e.getTitle())).toList();
        return new Result(new Result.ResultEvidence(evidence.getId(),
            evidence.getDescription(),
            evidenceType,
            user.getDisplayName(),
            evidence.getCreationTime(),
            evidence.getLastModificationTime()),
            new Result.ResultQuestion(
                question.id(),
                question.title(),
                question.index(),
                questionAnswerOptions,
                new Result.ResultQuestion.QuestionQuestionnaire(questionnaire.id(), questionnaire.title()),
                resultQuestionAnswer
            ));
    }
}
