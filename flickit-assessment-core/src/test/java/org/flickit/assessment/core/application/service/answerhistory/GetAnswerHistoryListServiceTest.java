package org.flickit.assessment.core.application.service.answerhistory;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.Answer;
import org.flickit.assessment.core.application.domain.FullUser;
import org.flickit.assessment.core.application.port.in.answerhistory.GetAnswerHistoryListUseCase;
import org.flickit.assessment.core.application.port.out.answerhistory.LoadAnswerHistoryListPort;
import org.flickit.assessment.core.application.port.out.minio.CreateFileDownloadLinkPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.core.test.fixture.application.AnswerMother.answerWithNotApplicableFalse;
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

    private final GetAnswerHistoryListUseCase.Param param = createParam(GetAnswerHistoryListUseCase.Param.ParamBuilder::build);

    @Test
    void testGetAnswerHistoryList_whenCurrentUserDoesNotHaveTheRequiredPermission_thenThrowAccessDeniedException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.VIEW_ANSWER_HISTORY_LIST))
            .thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> getAnswerHistoryListService.getAnswerHistoryList(param));
    }

    @Test
    void testGetAnswerHistoryList_whenCurrentUserHasTheRequiredPermission_thenReturnAnswerHistoryList() {
        var history1 = history(answerWithNotApplicableFalse(optionOne()));
        var history2 = history(answerWithQuestionIdAndNotApplicableTrue(param.getQuestionId()));

        var expectedHistory = new PaginatedResponse<>(List.of(history2, history1), param.getPage(), param.getSize(), "desc", "creationTime", 2);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.VIEW_ANSWER_HISTORY_LIST))
            .thenReturn(true);
        when(loadAnswerHistoryListPort.load(param.getAssessmentId(), param.getQuestionId(), param.getPage(), param.getSize())).thenReturn(expectedHistory);

        String picDownloadLink = "downloadLink";
        when(createFileDownloadLinkPort.createDownloadLinkSafe(anyString(), any())).thenReturn(picDownloadLink);

        var result = getAnswerHistoryListService.getAnswerHistoryList(param);

        assertEquals(expectedHistory.getItems().size(), result.getItems().size());
        assertNull(result.getItems().getFirst().answer().selectedOption());
        assertEquals(history2.answer().getConfidenceLevelId(), result.getItems().getFirst().answer().confidenceLevel().getId());
        assertEquals(history2.createdBy().getId(), result.getItems().getFirst().createdBy().id());
        assertEquals(history2.createdBy().getDisplayName(), result.getItems().getFirst().createdBy().displayName());
        assertEquals(picDownloadLink, result.getItems().getFirst().createdBy().pictureLink());

        assertNotNull(result.getItems().get(1).answer().selectedOption());
        assertEquals(history1.answer().getSelectedOption().getId(), result.getItems().get(1).answer().selectedOption().id());
        assertEquals(history1.answer().getSelectedOption().getIndex(), result.getItems().get(1).answer().selectedOption().index());
        assertEquals(history1.answer().getConfidenceLevelId(), result.getItems().get(1).answer().confidenceLevel().getId());
        assertEquals(history1.createdBy().getId(), result.getItems().get(1).createdBy().id());
        assertEquals(history1.createdBy().getDisplayName(), result.getItems().get(1).createdBy().displayName());
        assertEquals(picDownloadLink, result.getItems().get(1).createdBy().pictureLink());

        assertEquals(2, result.getTotal());
        assertEquals(param.getSize(), result.getSize());
        assertEquals(param.getPage(), result.getPage());
        assertEquals(expectedHistory.getSort(), result.getSort());
        assertEquals(expectedHistory.getOrder(), result.getOrder());
    }

    private LoadAnswerHistoryListPort.Result history(Answer answer) {
        return new LoadAnswerHistoryListPort.Result(answer,
            new FullUser(UUID.randomUUID(), "displayName", "email@gmail.com", "path/path"),
            LocalDateTime.now(),
            answer != null && answer.getSelectedOption() != null ? answer.getSelectedOption().getId() : null,
            answer != null && answer.getSelectedOption() != null ? answer.getSelectedOption().getIndex() : null);
    }

    private GetAnswerHistoryListUseCase.Param createParam(Consumer<GetAnswerHistoryListUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetAnswerHistoryListUseCase.Param.ParamBuilder paramBuilder() {
        return GetAnswerHistoryListUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .questionId(2L)
            .size(10)
            .page(1)
            .currentUserId(UUID.randomUUID());
    }
}
