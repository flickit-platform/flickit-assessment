package org.flickit.flickitassessmentcore.application.service.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.domain.crud.PaginatedResponse;
import org.flickit.flickitassessmentcore.application.port.in.answer.GetAnswerListUseCase;
import org.flickit.flickitassessmentcore.application.port.out.LoadAnswersByAssessmentAndQuestionnaireIdPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetAnswerListService implements GetAnswerListUseCase {

    private final LoadAnswersByAssessmentAndQuestionnaireIdPort loadAnswersPort;

    @Override
    public PaginatedResponse<AnswerListItem> getAnswerList(Param param) {
        return loadAnswersPort.loadAnswersByAssessmentAndQuestionnaireIdPort(mapToPortParam(param));
    }

    private LoadAnswersByAssessmentAndQuestionnaireIdPort.Param mapToPortParam(Param param) {
        return new LoadAnswersByAssessmentAndQuestionnaireIdPort.Param(param.getAssessmentId(), param.getQuestionnaireId(), param.getPage(), param.getSize());
    }
}
