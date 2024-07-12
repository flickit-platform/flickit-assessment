package org.flickit.assessment.core.application.service.evidence;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.evidence.GetAttributeEvidenceListUseCase.AttributeEvidenceListItem;
import org.flickit.assessment.core.application.port.in.evidence.GetAttributeEvidenceListUseCase.Param;
import org.flickit.assessment.core.application.port.out.evidence.LoadAttributeEvidencesPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ATTRIBUTE_EVIDENCE_LIST;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAttributeEvidenceListServiceTest {

    @InjectMocks
    private GetAttributeEvidenceListService service;

    @Mock
    private LoadAttributeEvidencesPort loadAttributeEvidencesPort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Test
    void testGetAttributeEvidenceList_ResultsFound_ItemReturned() {
        Param param = new Param(UUID.randomUUID(),
            1L,
            "POSITIVE",
            UUID.randomUUID(),
            10,
            0);
        AttributeEvidenceListItem attributeEvidence = createAttributeEvidence();

        when(loadAttributeEvidencesPort.loadAttributeEvidences(param.getAssessmentId(),
            param.getAttributeId(),
            param.getPage(),
            param.getPage(),
            param.getSize()))
            .thenReturn(new PaginatedResponse<>(List.of(attributeEvidence),
                0,
                1,
                "lastModificationTime",
                "DESC",
                1));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ATTRIBUTE_EVIDENCE_LIST)).thenReturn(true);

        PaginatedResponse<AttributeEvidenceListItem> result =
            service.getAttributeEvidenceList(param);

        assertEquals(1, result.getItems().size());
    }


    @Test
    void testGetAttributeEvidenceList_CurrentUserHasNotAccessToAssessment_ThrowNotFoundException() {
        Param param = new Param(UUID.randomUUID(), 1L, "POSITIVE",  UUID.randomUUID(), 10, 0);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ATTRIBUTE_EVIDENCE_LIST)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getAttributeEvidenceList(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
        verify(loadAttributeEvidencesPort, never()).loadAttributeEvidences(any(), any(), anyInt(), anyInt(), anyInt());
    }

    private AttributeEvidenceListItem createAttributeEvidence() {
        return new AttributeEvidenceListItem(UUID.randomUUID(),"description",1);
    }
}
