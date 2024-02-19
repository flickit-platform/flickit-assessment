package org.flickit.assessment.core.application.service.evidence;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.evidence.GetEvidenceListUseCase.EvidenceListItem;
import org.flickit.assessment.core.application.port.in.evidence.GetEvidenceListUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessment.CheckAssessmentExistencePort;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencesPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetEvidenceListServiceTest {

    @InjectMocks
    private GetEvidenceListService service;
    @Mock
    private LoadEvidencesPort loadEvidencesPort;

    @Mock
    private CheckAssessmentExistencePort checkAssessmentExistencePort;

    @Test
    void testGetEvidenceList_ResultsFound_2ItemsReturned() {
        Long question1Id = 1L;
        EvidenceListItem evidence1Q1 = createEvidence();
        EvidenceListItem evidence2Q1 = createEvidence();
        UUID ASSESSMENT_ID = UUID.randomUUID();

        when(checkAssessmentExistencePort.existsById(ASSESSMENT_ID)).thenReturn(true);
        when(loadEvidencesPort.loadNotDeletedEvidences(question1Id, ASSESSMENT_ID, 0, 10))
            .thenReturn(new PaginatedResponse<>(
                List.of(evidence1Q1, evidence2Q1),
                0,
                2,
                "lastModificationTime",
                "DESC",
                2));

        PaginatedResponse<EvidenceListItem> result = service.getEvidenceList(new Param(question1Id, ASSESSMENT_ID, 10, 0));

        assertEquals(2, result.getItems().size());
    }

    @Test
    void testGetEvidenceList_ResultsFound_NoItemReturned() {
        Long QUESTION2_ID = 2L;
        UUID ASSESSMENT_ID = UUID.randomUUID();
        when(checkAssessmentExistencePort.existsById(ASSESSMENT_ID)).thenReturn(true);
        when(loadEvidencesPort.loadNotDeletedEvidences(QUESTION2_ID, ASSESSMENT_ID, 0, 10))
            .thenReturn(new PaginatedResponse<>(
                new ArrayList<>(),
                0,
                0,
                "lastModificationTime",
                "DESC",
                0));

        PaginatedResponse<EvidenceListItem> result = service.getEvidenceList(new Param(QUESTION2_ID, ASSESSMENT_ID, 10, 0));

        assertEquals(0, result.getItems().size());
    }

    @Test
    void testGetEvidenceList_InvalidAssessmentId_ThrowNotFoundException() {
        UUID ASSESSMENT_ID = UUID.randomUUID();
        Param param = new Param(0L, ASSESSMENT_ID, 10, 0);
        when(checkAssessmentExistencePort.existsById(ASSESSMENT_ID)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.getEvidenceList(param));
        verify(loadEvidencesPort, never()).loadNotDeletedEvidences(any(), any(), anyInt(), anyInt());
    }

    private EvidenceListItem createEvidence() {
        return new EvidenceListItem(
            UUID.randomUUID(),
            "desc",
            UUID.randomUUID(),
            UUID.randomUUID(),
            "type",
            LocalDateTime.now()
        );
    }
}
