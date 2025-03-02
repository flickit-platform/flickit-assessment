package org.flickit.assessment.core.application.service.evidence;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.flickit.assessment.core.application.port.in.evidence.GetCommentListUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.LoadUserRoleForAssessmentPort;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencesPort;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencesPort.EvidenceListItem;
import org.flickit.assessment.core.application.port.out.minio.CreateFileDownloadLinkPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static java.util.concurrent.ThreadLocalRandom.current;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_EVIDENCE_LIST;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetCommentListServiceTest {

    @InjectMocks
    private GetCommentListService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadEvidencesPort loadEvidencesPort;

    @Mock
    private LoadUserRoleForAssessmentPort loadUserRoleForAssessmentPort;

    @Mock
    private CreateFileDownloadLinkPort createFileDownloadLinkPort;

    private final Param param = createParam(Param.ParamBuilder::build);

    @Test
    void testGetCommentList_ResultsFound_2ItemsReturned() {
        var comment1Q1 = createComment(param.getCurrentUserId());
        var comment2Q1 = createComment(UUID.randomUUID());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_EVIDENCE_LIST))
            .thenReturn(true);
        when(loadEvidencesPort.loadNotDeletedComments(param.getQuestionId(), param.getAssessmentId(), param.getPage(), param.getSize()))
            .thenReturn(new PaginatedResponse<>(
                List.of(comment1Q1, comment2Q1),
                0,
                2,
                "lastModificationTime",
                "DESC",
                2));
        when(loadUserRoleForAssessmentPort.load(param.getAssessmentId(), param.getCurrentUserId()))
            .thenReturn(Optional.of(AssessmentUserRole.MANAGER));
        when(createFileDownloadLinkPort.createDownloadLink(any(), any())).thenReturn("pictureLink");

        var result = service.getCommentList(param);

        assertEquals(2, result.getItems().size());
        assertEquals(comment1Q1.id(), result.getItems().getFirst().id());
        assertEquals(comment1Q1.createdBy().id(), result.getItems().getFirst().createdBy().id());
        assertEquals(comment1Q1.description(), result.getItems().getFirst().description());
        assertEquals(comment1Q1.lastModificationTime(), result.getItems().getFirst().lastModificationTime());
        assertEquals(comment1Q1.attachmentsCount(), result.getItems().getFirst().attachmentsCount());
        assertTrue(result.getItems().getFirst().editable());
        assertTrue(result.getItems().getFirst().deletable());
        assertTrue(result.getItems().getFirst().resolvable());
        assertEquals(comment2Q1.id(), result.getItems().get(1).id());
        assertEquals(comment2Q1.createdBy().id(), result.getItems().get(1).createdBy().id());
        assertEquals(comment2Q1.description(), result.getItems().get(1).description());
        assertEquals(comment2Q1.lastModificationTime(), result.getItems().get(1).lastModificationTime());
        assertEquals(comment2Q1.attachmentsCount(), result.getItems().get(1).attachmentsCount());
        assertFalse(result.getItems().get(1).editable());
        assertFalse(result.getItems().get(1).deletable());
        assertTrue(result.getItems().get(1).resolvable());
        verify(createFileDownloadLinkPort, times(2)).createDownloadLink(anyString(), any(Duration.class));
    }

    @Test
    void testGetCommentList_ResultsFound_NoItemReturned() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_EVIDENCE_LIST))
            .thenReturn(true);
        when(loadEvidencesPort.loadNotDeletedComments(param.getQuestionId(), param.getAssessmentId(), param.getPage(), param.getSize()))
            .thenReturn(new PaginatedResponse<>(
                new ArrayList<>(),
                0,
                0,
                "lastModificationTime",
                "DESC",
                0));
        when(loadUserRoleForAssessmentPort.load(param.getAssessmentId(), param.getCurrentUserId()))
            .thenReturn(Optional.of(AssessmentUserRole.MANAGER));

        var result = service.getCommentList(param);

        assertEquals(0, result.getItems().size());
        verifyNoInteractions(createFileDownloadLinkPort);
    }

    @Test
    void testGetEvidenceList_InvalidUser_ThrowsException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_EVIDENCE_LIST))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getCommentList(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    private EvidenceListItem createComment(UUID createdBy) {
        return new EvidenceListItem(
            UUID.randomUUID(),
            "desc",
            null,
            LocalDateTime.now(),
            current().nextInt(1, 6),
            new LoadEvidencesPort.User(createdBy, "user1", "pictureLink"),
            null,
            null
        );
    }

    private Param createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .questionId(153L)
            .assessmentId(UUID.randomUUID())
            .page(0)
            .size(10)
            .currentUserId(UUID.randomUUID());
    }
}