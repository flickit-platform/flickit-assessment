package org.flickit.assessment.core.application.service.answerhistory;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.AnswerHistory;
import org.flickit.assessment.core.application.port.in.answerhistory.GetAnswerHistoryListUseCase;
import org.flickit.assessment.core.application.port.out.answerhistory.LoadAnswerHistoryListPort;
import org.flickit.assessment.core.application.port.out.minio.CreateFileDownloadLinkPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.core.test.fixture.application.AnswerHistoryMother.history;
import static org.flickit.assessment.core.test.fixture.application.AnswerMother.answerWithNotApplicableTrue;
import static org.flickit.assessment.core.test.fixture.application.AnswerMother.answerWithQuestionIdAndNotApplicableTrue;
import static org.flickit.assessment.core.test.fixture.application.AnswerOptionMother.optionOne;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAnswerHistoryListServiceTest {

    @InjectMocks
    private GetAnswerHistoryListService getAnswerHistoryListService;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAnswerHistoryListPort loadAnswerHistoryListPort;

    @Mock
    private CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Test
    void testGetAnswerHistoryList_WhenCurrentUserDoesNotHaveTheRequiredPermission_ThenThrowAccessDeniedException() {
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
    void testGetAnswerHistoryList_WhenCurrentUserHasTheRequiredPermission_ThenReturnAnswerHistoryList() {
        UUID assessmentId = UUID.randomUUID();
        long questionId = 1L;
        UUID currentUserId = UUID.randomUUID();
        int size = 5;
        int page = 1;
        var param = new GetAnswerHistoryListUseCase.Param(assessmentId, questionId, currentUserId, size, page);

        AnswerHistory history1 = history(answerWithNotApplicableTrue(optionOne()));
        AnswerHistory history2 = history(answerWithQuestionIdAndNotApplicableTrue(questionId));

        var expected = new PaginatedResponse<>(List.of(history2, history1), page, size, "desc", "creationTime", 2);

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, AssessmentPermission.VIEW_ANSWER_HISTORY_LIST))
            .thenReturn(true);
        when(loadAnswerHistoryListPort.load(assessmentId, questionId, page, size)).thenReturn(expected);

        String picDownloadLink = "downloadLink";
        when(createFileDownloadLinkPort.createDownloadLink(anyString(), any())).thenReturn(picDownloadLink);

        var result = getAnswerHistoryListService.getAnswerHistoryList(param);

        assertEquals(expected.getItems().size(), result.getItems().size());
        assertNull(result.getItems().getFirst().answer().selectedOption());
        assertEquals(history2.getAnswer().getConfidenceLevelId(), result.getItems().getFirst().answer().confidenceLevel().getId());
        assertEquals(history2.getCreatedBy().getId(), result.getItems().getFirst().createdBy().id());
        assertEquals(history2.getCreatedBy().getDisplayName(), result.getItems().getFirst().createdBy().displayName());
        assertEquals(picDownloadLink, result.getItems().getFirst().createdBy().pictureLink());

        assertNotNull(result.getItems().get(1).answer().selectedOption());
        assertEquals(history1.getAnswer().getSelectedOption().getId(), result.getItems().get(1).answer().selectedOption().id());
        assertEquals(history1.getAnswer().getConfidenceLevelId(), result.getItems().get(1).answer().confidenceLevel().getId());
        assertEquals(history1.getCreatedBy().getId(), result.getItems().get(1).createdBy().id());
        assertEquals(history1.getCreatedBy().getDisplayName(), result.getItems().get(1).createdBy().displayName());
        assertEquals(picDownloadLink, result.getItems().get(1).createdBy().pictureLink());
    }
}
