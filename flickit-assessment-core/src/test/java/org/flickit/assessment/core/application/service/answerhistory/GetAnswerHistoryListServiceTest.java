package org.flickit.assessment.core.application.service.answerhistory;

import jakarta.validation.constraints.NotNull;
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
        var history1 = toHistory(answerWithNotApplicableFalse(optionOne()));
        var history2 = toHistory(answerWithQuestionIdAndNotApplicableTrue(param.getQuestionId()));

        var expectedHistories = new PaginatedResponse<>(List.of(history2, history1),
            param.getPage(),
            param.getSize(),
            "desc",
            "creationTime",
            2);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.VIEW_ANSWER_HISTORY_LIST))
            .thenReturn(true);
        when(loadAnswerHistoryListPort.load(param.getAssessmentId(), param.getQuestionId(), param.getPage(), param.getSize())).thenReturn(expectedHistories);

        String picDownloadLink = "downloadLink";
        when(createFileDownloadLinkPort.createDownloadLinkSafe(anyString(), any())).thenReturn(picDownloadLink);

        var result = getAnswerHistoryListService.getAnswerHistoryList(param);

        assertEquals(expectedHistories.getItems().size(), result.getItems().size());

        assertNull(result.getItems().getFirst().answer().selectedOption());
        assertEquals(history2.confidenceLevelId(), result.getItems().getFirst().answer().confidenceLevel().getId());
        assertEquals(history2.createdBy().getId(), result.getItems().getFirst().createdBy().id());
        assertEquals(history2.createdBy().getDisplayName(), result.getItems().getFirst().createdBy().displayName());
        assertEquals(picDownloadLink, result.getItems().getFirst().createdBy().pictureLink());

        assertNotNull(result.getItems().get(1).answer().selectedOption());
        assertEquals(history1.answerOptionId(), result.getItems().get(1).answer().selectedOption().id());
        assertEquals(history1.answerOptionIndex(), result.getItems().get(1).answer().selectedOption().index());
        assertEquals(history1.confidenceLevelId(), result.getItems().get(1).answer().confidenceLevel().getId());
        assertEquals(history1.createdBy().getId(), result.getItems().get(1).createdBy().id());
        assertEquals(history1.createdBy().getDisplayName(), result.getItems().get(1).createdBy().displayName());
        assertEquals(picDownloadLink, result.getItems().get(1).createdBy().pictureLink());

        assertPaginationProps(result, expectedHistories);
    }

    private void assertPaginationProps(PaginatedResponse<GetAnswerHistoryListUseCase.AnswerHistoryListItem> result,
                                       PaginatedResponse<LoadAnswerHistoryListPort.Result> expectedHistories) {
        assertEquals(2, result.getTotal());
        assertEquals(param.getSize(), result.getSize());
        assertEquals(param.getPage(), result.getPage());
        assertEquals(expectedHistories.getSort(), result.getSort());
        assertEquals(expectedHistories.getOrder(), result.getOrder());
    }

    private LoadAnswerHistoryListPort.Result toHistory(@NotNull Answer answer) {
        return new LoadAnswerHistoryListPort.Result(answer.getSelectedOption() != null ? answer.getSelectedOption().getId() : null,
            answer.getSelectedOption() != null ? answer.getSelectedOption().getIndex() : null,
            answer.getConfidenceLevelId(),
            answer.getIsNotApplicable(),
            answer.getAnswerStatus(),
            new FullUser(UUID.randomUUID(), "displayName", "email@gmail.com", "path/path"),
            LocalDateTime.now());
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
