package org.flickit.flickitassessmentcore.application.service.evidence;

import jakarta.validation.ConstraintViolationException;
import org.flickit.flickitassessmentcore.application.domain.crud.PaginatedResponse;
import org.flickit.flickitassessmentcore.application.port.in.evidence.GetEvidenceListUseCase;
import org.flickit.flickitassessmentcore.application.port.in.evidence.GetEvidenceListUseCase.EvidenceListItem;
import org.flickit.flickitassessmentcore.application.port.out.evidence.LoadEvidencesByQuestionPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
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

    private final LoadEvidencesByQuestionPort loadEvidencesByQuestion = Mockito.mock(LoadEvidencesByQuestionPort.class);

    private final GetEvidenceListService service = new GetEvidenceListService(loadEvidencesByQuestion);

    private final Long QUESTION1_ID = 1L;
    private final Long QUESTION2_ID = 2L;
    private final String DESC = "desc";

    private final EvidenceListItem evidence1Q1 = new EvidenceListItem(
        UUID.randomUUID(),
        DESC,
        1L,
        UUID.randomUUID(),
        LocalDateTime.now()
    );
    private final EvidenceListItem evidence2Q1 = new EvidenceListItem(
        UUID.randomUUID(),
        DESC,
        1L,
        UUID.randomUUID(),
        LocalDateTime.now()
    );

    @Test
    void getEvidenceList_ResultsFound_2ItemsReturned() {
        when(loadEvidencesByQuestion.loadEvidencesByQuestionId(QUESTION1_ID, 0, 10))
            .thenReturn(new PaginatedResponse<>(
                List.of(evidence1Q1, evidence2Q1),
                0,
                2,
                "lastModificationTime",
                "DESC",
                2));

        PaginatedResponse<EvidenceListItem> result = service.getEvidenceList(new GetEvidenceListUseCase.Param(QUESTION1_ID, 10, 0));

        assertEquals(2, result.getItems().size());
    }

    @Test
    void getEvidenceList_ResultsFound_0ItemReturned() {
        when(loadEvidencesByQuestion.loadEvidencesByQuestionId(QUESTION2_ID, 0, 10))
            .thenReturn(new PaginatedResponse<>(
                new ArrayList<>(),
                0,
                0,
                "lastModificationTime",
                "DESC",
                0));

        PaginatedResponse<EvidenceListItem> result = service.getEvidenceList(new GetEvidenceListUseCase.Param(QUESTION2_ID, 10, 0));

        assertEquals(0, result.getItems().size());
    }

    @Test
    void getEvidenceList_NullQuestion_ReturnErrorMessage() {
        GetEvidenceListUseCase.Param param = new GetEvidenceListUseCase.Param(null, 10, 0);
        assertThrows(ConstraintViolationException.class, () -> service.getEvidenceList(param));
    }
}
