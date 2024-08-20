package org.flickit.assessment.core.application.service.evidence;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.evidence.GetEvidenceListUseCase;
import org.flickit.assessment.core.application.port.in.evidence.GetEvidenceListUseCase.EvidenceListItem;
import org.flickit.assessment.core.application.port.in.evidence.GetEvidenceListUseCase.Param;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencesPort;
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
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_EVIDENCE_LIST;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetEvidenceListServiceTest {

    @InjectMocks
    private GetEvidenceListService service;

    @Mock
    private LoadEvidencesPort loadEvidencesPort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Test
    void testGetEvidenceList_ResultsFound_2ItemsReturned() {
        Long question1Id = 1L;
        EvidenceListItem evidence1Q1 = createEvidenceWithType("Positive");
        EvidenceListItem evidence2Q1 = createEvidenceWithType("Negative");
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_EVIDENCE_LIST)).thenReturn(true);
        when(loadEvidencesPort.loadNotDeletedEvidences(question1Id, assessmentId, 0, 10))
            .thenReturn(new PaginatedResponse<>(
                List.of(evidence1Q1, evidence2Q1),
                0,
                2,
                "lastModificationTime",
                "DESC",
                2));
        when(createFileDownloadLinkPort.createDownloadLink(any(), any())).thenReturn("pictureLink");

        PaginatedResponse<EvidenceListItem> result = service.getEvidenceList(new Param(question1Id, assessmentId, 10, 0, currentUserId));

        assertEquals(2, result.getItems().size());
        assertEquals(evidence1Q1.id(), result.getItems().getFirst().id());
        assertEquals(evidence1Q1.type(), result.getItems().getFirst().type());
        assertEquals(evidence1Q1.createdBy(), result.getItems().getFirst().createdBy());
        assertEquals(evidence1Q1.description(), result.getItems().getFirst().description());
        assertEquals(evidence1Q1.lastModificationTime(), result.getItems().getFirst().lastModificationTime());
        assertEquals(evidence1Q1.attachmentsCount(), result.getItems().getFirst().attachmentsCount());
        assertEquals(evidence2Q1.id(), result.getItems().get(1).id());
        assertEquals(evidence2Q1.type(), result.getItems().get(1).type());
        assertEquals(evidence2Q1.createdBy(), result.getItems().get(1).createdBy());
        assertEquals(evidence2Q1.description(), result.getItems().get(1).description());
        assertEquals(evidence2Q1.lastModificationTime(), result.getItems().get(1).lastModificationTime());
        assertEquals(evidence2Q1.attachmentsCount(), result.getItems().get(1).attachmentsCount());
        verify(createFileDownloadLinkPort, times(2)).createDownloadLink(anyString(), any(Duration.class));
    }

    @Test
    void testGetEvidenceList_ResultsFound_NoItemReturned() {
        Long QUESTION2_ID = 2L;
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_EVIDENCE_LIST)).thenReturn(true);
        when(loadEvidencesPort.loadNotDeletedEvidences(QUESTION2_ID, assessmentId, 0, 10))
            .thenReturn(new PaginatedResponse<>(
                new ArrayList<>(),
                0,
                0,
                "lastModificationTime",
                "DESC",
                0));

        PaginatedResponse<EvidenceListItem> result = service.getEvidenceList(new Param(QUESTION2_ID, assessmentId, 10, 0, currentUserId));

        assertEquals(0, result.getItems().size());
        verifyNoInteractions(createFileDownloadLinkPort);
    }

    @Test
    void testGetEvidenceList_InvalidUser_ThrowsException() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();

        var param = new GetEvidenceListUseCase.Param(123L, assessmentId, 10,0,currentUserId);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getEvidenceList(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    private EvidenceListItem createEvidenceWithType(String type) {
        return new EvidenceListItem(
            UUID.randomUUID(),
            "desc",
            type,
            LocalDateTime.now(),
            (int) (Math.random() * 5) + 1,
            new GetEvidenceListUseCase.User(UUID.randomUUID(), "user1", "pictureLink")
        );
    }
}
