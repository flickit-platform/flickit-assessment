package org.flickit.assessment.core.application.service.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.domain.Answer;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.ConfidenceLevel;
import org.flickit.assessment.core.application.domain.Evidence;
import org.flickit.assessment.core.application.port.in.evidence.GetEvidenceUseCase;
import org.flickit.assessment.core.application.port.out.answer.LoadAnswerPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencePort;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionPort;
import org.flickit.assessment.core.application.port.out.questionnaire.LoadQuestionnairePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetEvidenceService implements GetEvidenceUseCase {

    private final LoadEvidencePort loadEvidencePort;
    private final LoadAnswerPort loadAnswerPort;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    //private final CheckAssessmentSpaceMembershipPort checkAssessmentSpaceMembershipPort;
    private final LoadQuestionPort loadQuestionPort;
    private final LoadQuestionnairePort loadQuestionnairePort;

    @Override
    public Result getEvidence(Param param) {
        var evidencePortResult = loadEvidencePort.loadNotDeletedEvidence(param.getId());

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(evidencePortResult.getAssessmentId()).orElseThrow(); //TODO

        var question = loadQuestionPort.loadByIdAndKitVersionId(evidencePortResult.getQuestionId(), assessmentResult.getKitVersionId());

        var questionnaire = loadQuestionnairePort.loadByIdAndKitVersionId(question.questionnaireId(), assessmentResult.getKitVersionId()).orElseThrow();

        var answer = loadAnswerPort.load(assessmentResult.getId() , evidencePortResult.getQuestionId()).orElseThrow();

        /*if (!checkAssessmentSpaceMembershipPort.isAssessmentSpaceMember(evidencePortResult.getAssessmentId() ,param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);*/

        return mapToResult(evidencePortResult, assessmentResult, question, questionnaire, answer);
    }

    Result mapToResult(Evidence evidence, AssessmentResult assessmentResult, LoadQuestionPort.Result question, LoadQuestionnairePort.Result questionnaire, Answer answer) {
        var confidenceLevel = ConfidenceLevel.valueOfById(answer.getConfidenceLevelId()).getTitle();
        var evidenceType = (evidence.getType()!= null) ? EvidenceType.values()[evidence.getType()].getTitle() : null;
        return new Result(new Result.ResultEvidence(evidence.getId(),
            evidence.getDescription(),
            evidenceType,
            "//TODO: ", //TODO:
            evidence.getCreationTime(),
            evidence.getLastModificationTime()),
            new Result.ResultQuestion(
                question.id(),
                question.title(),
                question.index(),
                null, //TODO:
                new Result.ResultQuestion.QuestionQuestionnaire(questionnaire.id(), questionnaire.title()),
                new Result.ResultQuestion.ResultQuestionAnswer(null, confidenceLevel.getTitle())
            ));
    }
}
