package org.flickit.assessment.core.application.service.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.questionnaire.GetQuestionnairesProgressUseCase;
import org.flickit.assessment.core.application.port.out.questionnaire.GetQuestionnairesProgressPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_QUESTIONNAIRES_PROGRESS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetQuestionnairesProgressService implements GetQuestionnairesProgressUseCase {

    private final GetQuestionnairesProgressPort getQuestionnairesProgressPort;
    private final AssessmentAccessChecker assessmentAccessChecker;

    @Override
    public Result getQuestionnairesProgress(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_QUESTIONNAIRES_PROGRESS))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        var questionnairesProgress = getQuestionnairesProgressPort.getQuestionnairesProgressByAssessmentId(param.getAssessmentId());
        return new Result(questionnairesProgress);
    }
}
