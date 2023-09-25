package org.flickit.flickitassessmentcore.application.service.evidence;

import org.flickit.flickitassessmentcore.application.domain.crud.PaginatedResponse;
import org.flickit.flickitassessmentcore.application.port.in.evidence.GetEvidenceListUseCase.EvidenceListItem;
import org.flickit.flickitassessmentcore.application.port.in.evidence.GetEvidenceListUseCase.Param;
import org.flickit.flickitassessmentcore.application.port.out.assessment.CheckAssessmentExistencePort;
import org.flickit.flickitassessmentcore.application.port.out.evidence.LoadEvidencesByQuestionAndAssessmentPort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
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
    private LoadEvidencesByQuestionAndAssessmentPort loadEvidencesPort;

    @Mock
    private CheckAssessmentExistencePort checkAssessmentExistencePort;

    @Test
    void testGetEvidenceList_ResultsFound_2ItemsReturned() {
        Long question1Id = 1L;
        EvidenceListItem evidence1Q1 = createEvidence();
        EvidenceListItem evidence2Q1 = createEvidence();
        UUID ASSESSMENT_ID = UUID.randomUUID();

        when(checkAssessmentExistencePort.existsById(ASSESSMENT_ID)).thenReturn(true);
        when(loadEvidencesPort.loadEvidencesByQuestionIdAndAssessmentId(question1Id, ASSESSMENT_ID, 0, 10))
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
        when(loadEvidencesPort.loadEvidencesByQuestionIdAndAssessmentId(QUESTION2_ID, ASSESSMENT_ID, 0, 10))
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
    void getEvidenceList_InvalidAssessmentId_ThrowNotFoundException() {
        UUID ASSESSMENT_ID = UUID.randomUUID();
        Param param = new Param(0L, ASSESSMENT_ID, 10, 0);
        when(checkAssessmentExistencePort.existsById(ASSESSMENT_ID)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.getEvidenceList(param));
        verify(loadEvidencesPort, never()).loadEvidencesByQuestionIdAndAssessmentId(any(), any(), anyInt(), anyInt());
    }

    private EvidenceListItem createEvidence() {
        return new EvidenceListItem(
            UUID.randomUUID(),
            "desc",
            1L,
            UUID.randomUUID(),
            LocalDateTime.now()
        );
    }
}
