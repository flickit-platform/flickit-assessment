package org.flickit.assessment.core.application.service.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.in.questionnaire.GetQuestionnairesProgressUseCase;
import org.flickit.assessment.core.application.port.out.questionnaire.GetQuestionnairesProgressPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetQuestionnairesProgressService implements GetQuestionnairesProgressUseCase {

    private final GetQuestionnairesProgressPort getQuestionnairesProgressPort;

    @Override
    public Result getQuestionnairesProgress(Param param) {
        var questionnairesProgress = getQuestionnairesProgressPort.getQuestionnairesProgressByAssessmentId(param.getAssessmentId());
        return new Result(questionnairesProgress);
    }
}
