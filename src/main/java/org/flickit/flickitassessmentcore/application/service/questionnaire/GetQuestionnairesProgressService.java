package org.flickit.flickitassessmentcore.application.service.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.questionnaire.GetQuestionnairesProgressUseCase;
import org.flickit.flickitassessmentcore.application.port.out.questionnaire.GetQuestionnairesProgressByAssessmentPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetQuestionnairesProgressService implements GetQuestionnairesProgressUseCase {

    private final GetQuestionnairesProgressByAssessmentPort questionnairesProgressPort;

    @Override
    public Result getQuestionnairesProgress(Param param) {
        var questionnairesProgress = questionnairesProgressPort.getQuestionnairesProgressByAssessmentId(param.getAssessmentId());
        return new Result(questionnairesProgress);
    }
}
