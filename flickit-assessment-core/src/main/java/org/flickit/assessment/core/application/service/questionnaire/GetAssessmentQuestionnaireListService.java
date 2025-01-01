package org.flickit.assessment.core.application.service.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.ConfidenceLevel;
import org.flickit.assessment.core.application.domain.QuestionnaireListItem;
import org.flickit.assessment.core.application.port.in.questionnaire.GetAssessmentQuestionnaireListUseCase;
import org.flickit.assessment.core.application.port.out.answer.CountLowConfidenceAnswersPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.questionnaire.LoadQuestionnairesByAssessmentIdPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_QUESTIONNAIRE_LIST;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_QUESTIONNAIRE_LIST_ASSESSMENT_RESULT_ID_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentQuestionnaireListService implements GetAssessmentQuestionnaireListUseCase {

    private final LoadQuestionnairesByAssessmentIdPort loadQuestionnairesByAssessmentIdPort;
    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final CountLowConfidenceAnswersPort lowConfidenceAnswersPort;

    @Override
    public PaginatedResponse<QuestionnaireListItem> getAssessmentQuestionnaireList(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_QUESTIONNAIRE_LIST))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_ASSESSMENT_QUESTIONNAIRE_LIST_ASSESSMENT_RESULT_ID_NOT_FOUND));

        var questionnaires = loadQuestionnairesByAssessmentIdPort.loadAllByAssessmentId(toPortParam(param, assessmentResult));

        return buildResultWithIssues(questionnaires, assessmentResult.getId());
    }
    private PaginatedResponse<QuestionnaireListItem> buildResultWithIssues(PaginatedResponse<QuestionnaireListItem> questionnaires, UUID assessmentResultId ) {
        var questionnaireIds = questionnaires.getItems().stream().map(QuestionnaireListItem::id).collect(Collectors.toCollection(ArrayList::new));
        var lowConfidenceAnswersCount = lowConfidenceAnswersPort.countByQuestionnaireIdWithConfidenceLessThan(assessmentResultId, questionnaireIds, ConfidenceLevel.SOMEWHAT_UNSURE);
        var items = questionnaires.getItems().stream().map(i -> buildQuestionnaireWithIssues(i, lowConfidenceAnswersCount)).toList();

        return new PaginatedResponse<>(items,
            questionnaires.getPage(),
            questionnaires.getSize(),
            questionnaires.getSort(),
            questionnaires.getOrder(),
            questionnaires.getTotal());
    }

    private QuestionnaireListItem buildQuestionnaireWithIssues(QuestionnaireListItem questionnaireListItem, Map<Long, Integer> lowConfidenceAnswersCount) {
        return new QuestionnaireListItem(questionnaireListItem.id(),
            questionnaireListItem.title(),
            questionnaireListItem.description(),
            questionnaireListItem.index(),
            questionnaireListItem.questionCount(),
            questionnaireListItem.answerCount(),
            questionnaireListItem.nextQuestion(),
            questionnaireListItem.progress(),
            questionnaireListItem.subjects(),
            buildIssues(questionnaireListItem, lowConfidenceAnswersCount.get(questionnaireListItem.id())));
    }

    private QuestionnaireListItem.Issues buildIssues(QuestionnaireListItem questionnaireListItem, Integer answeredWithLowConfidence) {
        return new QuestionnaireListItem.Issues(questionnaireListItem.questionCount() - questionnaireListItem.answerCount(),
            answeredWithLowConfidence,
            0,
            0);
    }

    private LoadQuestionnairesByAssessmentIdPort.Param toPortParam(Param param, AssessmentResult assessmentResult) {
        return new LoadQuestionnairesByAssessmentIdPort.Param(
            param.getAssessmentId(),
            assessmentResult,
            param.getSize(),
            param.getPage());
    }
}
