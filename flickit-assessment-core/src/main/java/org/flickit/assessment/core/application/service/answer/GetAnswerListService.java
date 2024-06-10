package org.flickit.assessment.core.application.service.answer;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.Answer;
import org.flickit.assessment.core.application.domain.ConfidenceLevel;
import org.flickit.assessment.core.application.port.in.answer.GetAnswerListUseCase;
import org.flickit.assessment.core.application.port.out.answer.LoadAnswerListPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ANSWER;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAnswerListService implements GetAnswerListUseCase {

    private final LoadAnswerListPort loadAnswerListPort;
    private final AssessmentAccessChecker assessmentAccessChecker;

    @Override
    public PaginatedResponse<AnswerListItem> getAnswerList(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ANSWER))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
        var answerPaginatedResponse = loadAnswerListPort.loadByQuestionnaire(param.getAssessmentId(),
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
            x.getSelectedOption() != null ? x.getSelectedOption().getId() : null,
            ConfidenceLevel.valueOfById(x.getConfidenceLevelId()),
            x.getIsNotApplicable());
    }
}
