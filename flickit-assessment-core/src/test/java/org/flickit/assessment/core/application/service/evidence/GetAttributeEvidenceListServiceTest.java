package org.flickit.assessment.core.application.service.evidence;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.evidence.GetAttributeEvidenceListUseCase.Param;
import org.flickit.assessment.core.application.port.in.evidence.GetAttributeEvidenceListUseCase.AttributeEvidenceListItem;
import org.flickit.assessment.core.application.port.out.assessment.CheckAssessmentExistencePort;
import org.flickit.assessment.core.application.port.out.evidence.LoadAttributeEvidencesPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
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
    private CheckAssessmentExistencePort checkAssessmentExistencePort;

    @Test
    void testGetAttributeEvidenceList_ResultsFound_ItemReturned() {
        AttributeEvidenceListItem attributeEvidence = createAttributeEvidence();

        when(checkAssessmentExistencePort.existsById(any(UUID.class))).thenReturn(true);
        when(loadAttributeEvidencesPort.loadAttributeEvidences(any(UUID.class),
            any(Long.class),
            anyInt(),
            anyInt()))
            .thenReturn(new PaginatedResponse<>(List.of(attributeEvidence),
                0,
                1,
                "lastModificationTime",
                "DESC",
                1));

        PaginatedResponse<AttributeEvidenceListItem> result =
            service.getAttributeEvidenceList(new Param(UUID.randomUUID(), 1L, 10, 0));

        assertEquals(1, result.getItems().size());
    }

    @Test
    void testGetAttributeEvidenceList_InvalidAssessmentId_ThrowNotFoundException() {
        Param param = new Param(UUID.randomUUID(), 1L, 10, 0);
        when(checkAssessmentExistencePort.existsById(any(UUID.class))).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.getAttributeEvidenceList(param));
        verify(loadAttributeEvidencesPort, never()).loadAttributeEvidences(any(), any(), anyInt(), anyInt());
    }

    private AttributeEvidenceListItem createAttributeEvidence() {
        return new AttributeEvidenceListItem(
            "description",
            "title"
        );
    }
}
