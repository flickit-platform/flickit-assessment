package org.flickit.flickitassessmentcore.application.service.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.domain.crud.PaginatedResponse;
import org.flickit.flickitassessmentcore.application.port.in.answer.GetAnswerListUseCase;
import org.flickit.flickitassessmentcore.application.port.out.LoadAnswersByQuestionnaireIdPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetAnswerListService implements GetAnswerListUseCase {

    private final LoadAnswersByQuestionnaireIdPort loadAnswersPort;

    @Override
    public PaginatedResponse<AnswerListItem> getAnswerList(Param param) {
        return loadAnswersPort.loadAnswersByQuestionnaireId(mapToPortParam(param));
    }

    private LoadAnswersByQuestionnaireIdPort.Param mapToPortParam(Param param) {
        return new LoadAnswersByQuestionnaireIdPort.Param(param.getAssessmentId(),
            param.getQuestionnaireId(),
            param.getPage(),
            param.getSize());
    }
}
