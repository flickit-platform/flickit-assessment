package org.flickit.assessment.core.application.service.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.QuestionnaireListItem;
import org.flickit.assessment.core.application.port.in.questionnaire.GetAssessmentQuestionnaireListUseCase;
import org.flickit.assessment.core.application.port.out.questionnaire.LoadQuestionnairesByAssessmentIdPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_QUESTIONNAIRE_LIST;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentQuestionnaireListService implements GetAssessmentQuestionnaireListUseCase {

    private final LoadQuestionnairesByAssessmentIdPort loadQuestionnairesByAssessmentIdPort;
    private final AssessmentAccessChecker assessmentAccessChecker;

    @Override
    public PaginatedResponse<QuestionnaireListItem> getAssessmentQuestionnaireList(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_QUESTIONNAIRE_LIST))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var questionnaires = loadQuestionnairesByAssessmentIdPort.loadAllByAssessmentId(toPortParam(param));

        return buildResultWithIssues(questionnaires);
    }

    private PaginatedResponse<QuestionnaireListItem> buildResultWithIssues(PaginatedResponse<QuestionnaireListItem> questionnaires) {
        var items = questionnaires.getItems().stream().map(this::buildQuestionnaireWithIssues).toList();
        return new PaginatedResponse<>(items,
            questionnaires.getPage(),
            questionnaires.getSize(),
            questionnaires.getSort(),
            questionnaires.getOrder(),
            questionnaires.getTotal());
    }

    private QuestionnaireListItem buildQuestionnaireWithIssues(QuestionnaireListItem questionnaireListItem) {
        return new QuestionnaireListItem(questionnaireListItem.id(),
            questionnaireListItem.title(),
            questionnaireListItem.description(),
            questionnaireListItem.index(),
            questionnaireListItem.questionCount(),
            questionnaireListItem.answerCount(),
            questionnaireListItem.nextQuestion(),
            questionnaireListItem.progress(),
            questionnaireListItem.subjects(),
            buildIssues(questionnaireListItem));
    }

    private QuestionnaireListItem.Issues buildIssues(QuestionnaireListItem questionnaireListItem) {
        return new QuestionnaireListItem.Issues(questionnaireListItem.questionCount() - questionnaireListItem.answerCount(),
            0,
            0,
            0);
    }

    private LoadQuestionnairesByAssessmentIdPort.Param toPortParam(Param param) {
        return new LoadQuestionnairesByAssessmentIdPort.Param(
            param.getAssessmentId(),
            param.getSize(),
            param.getPage());
    }
}
