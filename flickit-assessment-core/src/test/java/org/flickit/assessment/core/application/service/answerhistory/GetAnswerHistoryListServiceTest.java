package org.flickit.assessment.core.application.service.answerhistory;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.ConfidenceLevel;
import org.flickit.assessment.core.application.port.in.answerhistory.GetAnswerHistoryListUseCase;
import org.flickit.assessment.core.application.port.out.answerhistory.LoadAnswerHistoryListPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAnswerHistoryListServiceTest {

    @InjectMocks
    private GetAnswerHistoryListService getAnswerHistoryListService;

    @Mock
    private LoadAnswerHistoryListPort loadAnswerHistoryListPort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Test
    void testGetAnswerHistoryList_WhenCurrentUserDoesntHaveViewAnswerHistoryListPermission_ThenThrowAccessDeniedException() {
        UUID assessmentId = UUID.randomUUID();
        long questionId = 1L;
        UUID currentUserId = UUID.randomUUID();
        int size = 5;
        int page = 1;
        var param = new GetAnswerHistoryListUseCase.Param(assessmentId, questionId, currentUserId, size, page);

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, AssessmentPermission.VIEW_ANSWER_HISTORY_LIST))
            .thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> getAnswerHistoryListService.getAnswerHistoryList(param));
    }

    @Test
    void testGetAnswerHistoryList_WhenCurrentUserHaveViewAnswerHistoryListPermission_ThenReturnAnswerHistoryList() {
        UUID assessmentId = UUID.randomUUID();
        long questionId = 1L;
        UUID currentUserId = UUID.randomUUID();
        int size = 5;
        int page = 1;
        var param = new GetAnswerHistoryListUseCase.Param(assessmentId, questionId, currentUserId, size, page);

        LocalDateTime submitTime = LocalDateTime.now();
        String submitterName = "flickit admin";
        String confidenceLevel = ConfidenceLevel.COMPLETELY_SURE.getTitle();
        int answerOptionIndex = 1;
        boolean isNotApplicable = false;

        var items = new LoadAnswerHistoryListPort.AnswerHistoryListItem(submitTime,
            submitterName,
            confidenceLevel,
            answerOptionIndex,
            isNotApplicable);
        String sort = "modifiedAt";
        String order = "desc";
        int total = 1;

        var expected = new PaginatedResponse<>(List.of(items), page, size, sort, order, total);

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, AssessmentPermission.VIEW_ANSWER_HISTORY_LIST))
            .thenReturn(true);
        when(loadAnswerHistoryListPort.loadByAssessmentIdAndQuestionId(assessmentId, questionId, page, size))
            .thenReturn(expected);

        var actual = getAnswerHistoryListService.getAnswerHistoryList(param);
        assertEquals(expected.getItems().size(), actual.getItems().size());
        var expectedItem = expected.getItems().get(0);
        var actualItem = actual.getItems().get(0);
        assertEquals(expectedItem.submitTime(), actualItem.submitTime());
        assertEquals(expectedItem.submitterName(), actualItem.submitterName());
        assertEquals(expectedItem.confidenceLevel(), actualItem.confidenceLevel());
        assertEquals(expectedItem.answerOptionIndex(), actualItem.answerOptionIndex());
        assertEquals(expectedItem.isNotApplicable(), actualItem.isNotApplicable());
    }
}
