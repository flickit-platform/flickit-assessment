package org.flickit.flickitassessmentcore.application.service.evidence;

import jakarta.validation.ConstraintViolationException;
import org.flickit.flickitassessmentcore.application.domain.crud.PaginatedResponse;
import org.flickit.flickitassessmentcore.application.port.in.evidence.GetEvidenceListUseCase;
import org.flickit.flickitassessmentcore.application.port.in.evidence.GetEvidenceListUseCase.EvidenceListItem;
import org.flickit.flickitassessmentcore.application.port.out.evidence.LoadEvidencesByQuestionAndAssessmentPort;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetEvidenceListServiceTest {

    @InjectMocks
    private GetEvidenceListService service;
    @Mock
    private LoadEvidencesByQuestionAndAssessmentPort loadEvidencesPort;

    @Test
    void getEvidenceList_ResultsFound_2ItemsReturned() {
        Long question1Id = 1L;
        EvidenceListItem evidence1Q1 = createEvidence();
        EvidenceListItem evidence2Q1 = createEvidence();
        UUID ASSESSMENT_ID = UUID.randomUUID();

        when(loadEvidencesPort.loadEvidencesByQuestionIdAndAssessmentId(question1Id, ASSESSMENT_ID, 0, 10))
            .thenReturn(new PaginatedResponse<>(
                List.of(evidence1Q1, evidence2Q1),
                0,
                2,
                "lastModificationTime",
                "DESC",
                2));

        PaginatedResponse<EvidenceListItem> result = service.getEvidenceList(new GetEvidenceListUseCase.Param(question1Id, ASSESSMENT_ID, 10, 0));

        assertEquals(2, result.getItems().size());
    }

    @Test
    void getEvidenceList_ResultsFound_0ItemReturned() {
        Long QUESTION2_ID = 2L;
        UUID ASSESSMENT_ID = UUID.randomUUID();
        when(loadEvidencesPort.loadEvidencesByQuestionIdAndAssessmentId(QUESTION2_ID, ASSESSMENT_ID, 0, 10))
            .thenReturn(new PaginatedResponse<>(
                new ArrayList<>(),
                0,
                0,
                "lastModificationTime",
                "DESC",
                0));

        PaginatedResponse<EvidenceListItem> result = service.getEvidenceList(new GetEvidenceListUseCase.Param(QUESTION2_ID, ASSESSMENT_ID, 10, 0));

        assertEquals(0, result.getItems().size());
    }

    @Test
    void getEvidenceList_NullQuestion_ReturnErrorMessage() {
        UUID ASSESSMENT_ID = UUID.randomUUID();
        assertThrows(ConstraintViolationException.class, () -> new GetEvidenceListUseCase.Param(null, ASSESSMENT_ID, 10, 0));
    }

    @Test
    void getEvidenceList_NullAssessment_ReturnErrorMessage() {
        assertThrows(ConstraintViolationException.class, () -> new GetEvidenceListUseCase.Param(0L, null, 10, 0));
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
