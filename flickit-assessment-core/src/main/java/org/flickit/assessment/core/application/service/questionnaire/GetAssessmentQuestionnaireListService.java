package org.flickit.assessment.core.application.service.questionnaire;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.ConfidenceLevel;
import org.flickit.assessment.core.application.domain.QuestionnaireListItem;
import org.flickit.assessment.core.application.port.in.questionnaire.GetAssessmentQuestionnaireListUseCase;
import org.flickit.assessment.core.application.port.out.answer.CountAnswersPort;
import org.flickit.assessment.core.application.port.out.answer.CountLowConfidenceAnswersPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.evidence.CountEvidencesPort;
import org.flickit.assessment.core.application.port.out.questionnaire.LoadQuestionnairesPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static java.util.stream.Collectors.toSet;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_QUESTIONNAIRE_LIST;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_QUESTIONNAIRE_LIST_ASSESSMENT_RESULT_ID_NOT_FOUND;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentQuestionnaireListService implements GetAssessmentQuestionnaireListUseCase {

    private final LoadQuestionnairesPort loadQuestionnairesPort;
    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final CountLowConfidenceAnswersPort lowConfidenceAnswersPort;
    private final CountEvidencesPort countEvidencesPort;
    private final CountAnswersPort countAnswersPort;

    @Override
    public PaginatedResponse<QuestionnaireListItem> getAssessmentQuestionnaireList(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_QUESTIONNAIRE_LIST))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_ASSESSMENT_QUESTIONNAIRE_LIST_ASSESSMENT_RESULT_ID_NOT_FOUND));

        var questionnaires = loadQuestionnairesPort.loadAllByAssessmentId(toPortParam(assessmentResult, param));
        return buildResultWithIssues(assessmentResult, questionnaires);
    }

    private LoadQuestionnairesPort.Param toPortParam(AssessmentResult assessmentResult, Param param) {
        return new LoadQuestionnairesPort.Param(
            assessmentResult,
            param.getSize(),
            param.getPage());
    }

    private PaginatedResponse<QuestionnaireListItem> buildResultWithIssues(AssessmentResult assessmentResult, PaginatedResponse<QuestionnaireListItem> questionnaires) {
        var questionnaireIds = questionnaires.getItems().stream()
            .map(QuestionnaireListItem::id)
            .collect(toSet());
        var questionnaireIdToLowConfidenceAnswersCount = lowConfidenceAnswersPort.countWithConfidenceLessThan(
            assessmentResult.getId(), questionnaireIds, ConfidenceLevel.SOMEWHAT_UNSURE);
        var questionnaireIdToUnresolvedCommentsCount = countEvidencesPort.countUnresolvedComments(
            assessmentResult.getAssessment().getId(), questionnaireIds);
        var questionnaireIdToEvidenceCount = countEvidencesPort.countAnsweredQuestionsHavingEvidence(
            assessmentResult.getAssessment().getId(), questionnaireIds);
        var questionnaireIdToUnapprovedAnswersCount = countAnswersPort.countUnapprovedAnswers(
            assessmentResult.getId(), questionnaireIds);

        var items = questionnaires.getItems().stream()
            .map(questionnaire -> {
                var issues = buildQuestionnaireIssues(questionnaire,
                    questionnaireIdToLowConfidenceAnswersCount,
                    questionnaireIdToUnresolvedCommentsCount,
                    questionnaireIdToEvidenceCount,
                    questionnaireIdToUnapprovedAnswersCount);
                return questionnaire.withIssues(issues);
            })
            .toList();

        return new PaginatedResponse<>(items,
            questionnaires.getPage(),
            questionnaires.getSize(),
            questionnaires.getSort(),
            questionnaires.getOrder(),
            questionnaires.getTotal());
    }

    private QuestionnaireListItem.Issues buildQuestionnaireIssues(QuestionnaireListItem questionnaire,
                                                                  Map<Long, Integer> lowConfidenceAnswersCount,
                                                                  Map<Long, Integer> questionnairesUnresolvedComments,
                                                                  Map<Long, Integer> questionnairesEvidenceCount,
                                                                  Map<Long, Integer> questionnairesUnapprovedAnswers) {
        int unanswered = Math.max(questionnaire.questionCount() - questionnaire.answerCount(), 0);
        if (questionnaire.answerCount() > questionnaire.questionCount())
            log.error("AnswerCount exceeds the questions count: answerCount:[{}], questionCount:[{}]",
                questionnaire.answerCount(), questionnaire.questionCount());

        int answeredWithLowConfidence = lowConfidenceAnswersCount.getOrDefault(questionnaire.id(), 0);

        int evidencesCount = questionnairesEvidenceCount.getOrDefault(questionnaire.id(), 0);
        int answeredWithoutEvidence = Math.max(questionnaire.answerCount() - evidencesCount, 0);
        if (evidencesCount > questionnaire.answerCount())
            log.error("EvidencesCount exceeds the answer count: evidencesCount:[{}], answerCount:[{}]",
                evidencesCount, questionnaire.answerCount());

        int unresolvedComments = questionnairesUnresolvedComments.getOrDefault(questionnaire.id(), 0);
        int unapprovedAnswers = questionnairesUnapprovedAnswers.getOrDefault(questionnaire.id(), 0);

        return new QuestionnaireListItem.Issues(unanswered,
            answeredWithLowConfidence,
            answeredWithoutEvidence,
            unresolvedComments,
            unapprovedAnswers);
    }
}
