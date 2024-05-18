package org.flickit.assessment.core.application.service.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.core.application.domain.Answer;
import org.flickit.assessment.core.application.domain.ConfidenceLevel;
import org.flickit.assessment.core.application.port.in.answer.GetAnswerListUseCase;
import org.flickit.assessment.core.application.port.out.answer.LoadQuestionnaireAnswerListPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAnswerListService implements GetAnswerListUseCase {

    private final LoadQuestionnaireAnswerListPort loadQuestionnaireAnswerListPort;

    @Override
    public PaginatedResponse<AnswerListItem> getAnswerList(Param param) {
        PaginatedResponse<Answer> answerPaginatedResponse = loadQuestionnaireAnswerListPort.loadQuestionnaireAnswers(param.getAssessmentId(),
            param.getQuestionnaireId(),
            param.getSize(),
            param.getPage());

        List<AnswerListItem> items = answerPaginatedResponse.getItems().stream().map(this::toAnswerListItem)
            .toList();
        return new PaginatedResponse<>(items,
            answerPaginatedResponse.getPage(),
            answerPaginatedResponse.getSize(),
            answerPaginatedResponse.getSort(),
            answerPaginatedResponse.getOrder(),
            answerPaginatedResponse.getTotal());
    }

    private AnswerListItem toAnswerListItem(Answer x) {
        return new AnswerListItem(x.getId(),
            x.getQuestionId(),
            x.getSelectedOption().getId(),
            ConfidenceLevel.valueOfById(x.getConfidenceLevelId()),
            x.getIsNotApplicable());
    }
}
