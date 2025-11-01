package org.flickit.assessment.core.application.service.answerhistory;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.ConfidenceLevel;
import org.flickit.assessment.core.application.domain.FullUser;
import org.flickit.assessment.core.application.port.in.answerhistory.GetAnswerHistoryListUseCase;
import org.flickit.assessment.core.application.port.out.answerhistory.LoadAnswerHistoryPort;
import org.flickit.assessment.core.application.port.out.minio.CreateFileDownloadLinkPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAnswerHistoryListService implements GetAnswerHistoryListUseCase {

    private static final Duration EXPIRY_DURATION = Duration.ofDays(1);

    private final LoadAnswerHistoryPort loadAnswerHistoryPort;
    private final AssessmentAccessChecker assessmentAccessChecker;
    private final CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Override
    public PaginatedResponse<AnswerHistoryListItem> getAnswerHistoryList(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(),
            param.getCurrentUserId(),
            AssessmentPermission.VIEW_ANSWER_HISTORY_LIST))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var paginatedResponse = loadAnswerHistoryPort.load(param.getAssessmentId(),
            param.getQuestionId(),
            param.getPage(),
            param.getSize());

        List<AnswerHistoryListItem> items = paginatedResponse.getItems().stream()
            .map(e -> new AnswerHistoryListItem(toAnswer(e),
                e.creationTime(),
                getAnswerUserInfo(e.createdBy())))
            .toList();

        return new PaginatedResponse<>(items,
            paginatedResponse.getPage(),
            paginatedResponse.getSize(),
            paginatedResponse.getSort(),
            paginatedResponse.getOrder(),
            paginatedResponse.getTotal());
    }

    public static Answer toAnswer(LoadAnswerHistoryPort.Result answerHistory) {
        return new Answer(answerHistory.answerOptionId() != null ? Option.of(answerHistory.answerOptionId(), answerHistory.answerOptionIndex()) : null,
            answerHistory.confidenceLevelId() != null ? ConfidenceLevel.valueOfById(answerHistory.confidenceLevelId()) : ConfidenceLevel.getDefault(),
            answerHistory.isNotApplicable());
    }

    private GetAnswerHistoryListUseCase.User getAnswerUserInfo(FullUser user) {
        return new GetAnswerHistoryListUseCase.User(user.getId(),
            user.getDisplayName(),
            createFileDownloadLinkPort.createDownloadLinkSafe(user.getPicturePath(), EXPIRY_DURATION));
    }
}
