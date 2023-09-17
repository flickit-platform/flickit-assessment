package org.flickit.flickitassessmentcore.application.service.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.domain.crud.PaginatedResponse;
import org.flickit.flickitassessmentcore.application.port.in.answer.GetAnswerListUseCase;
import org.flickit.flickitassessmentcore.application.port.out.answer.LoadAnswersByQuestionnaireIdPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetAnswerListService implements GetAnswerListUseCase {

    private final LoadAnswersByQuestionnaireIdPort loadAnswersPort;

    @Override
    public PaginatedResponse<AnswerListItem> getAnswerList(Param param) {
        return loadAnswersPort.loadAnswersByQuestionnaireId(toPortParam(param));
    }

    private LoadAnswersByQuestionnaireIdPort.Param toPortParam(Param param) {
        return new LoadAnswersByQuestionnaireIdPort.Param(param.getAssessmentId(),
            param.getQuestionnaireId(),
            param.getPage(),
            param.getSize());
    }
}
