package org.flickit.assessment.core.application.service.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.QuestionnaireListItem;
import org.flickit.assessment.core.application.port.in.questionnaire.GetAssessmentQuestionnaireListUseCase;
import org.flickit.assessment.core.application.port.out.assessment.CheckUserAssessmentAccessPort;
import org.flickit.assessment.core.application.port.out.questionnaire.LoadQuestionnairesByAssessmentIdPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentQuestionnaireListService implements GetAssessmentQuestionnaireListUseCase {

    private final CheckUserAssessmentAccessPort checkUserAssessmentAccessPort;
    private final LoadQuestionnairesByAssessmentIdPort loadQuestionnairesByAssessmentIdPort;

    @Override
    public PaginatedResponse<QuestionnaireListItem> getAssessmentQuestionnaireList(Param param) {
        if (!checkUserAssessmentAccessPort.hasAccess(param.getAssessmentId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        return loadQuestionnairesByAssessmentIdPort.loadAllByAssessmentId(toPortParam(param));
    }

    private LoadQuestionnairesByAssessmentIdPort.Param toPortParam(Param param) {
        return new LoadQuestionnairesByAssessmentIdPort.Param(
            param.getAssessmentId(),
            param.getSize(),
            param.getPage());
    }
}
