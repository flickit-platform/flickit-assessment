package org.flickit.flickitassessmentcore.application.service.evidence;

import jakarta.validation.ConstraintViolationException;
import org.flickit.flickitassessmentcore.application.port.in.evidence.GetEvidenceListUseCase;
import org.flickit.flickitassessmentcore.application.port.out.evidence.LoadEvidencesByQuestionPort;
import org.flickit.flickitassessmentcore.domain.Evidence;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetEvidenceListServiceTest {

    private final LoadEvidencesByQuestionPort loadEvidencesByQuestion = Mockito.mock(LoadEvidencesByQuestionPort.class);

    private final GetEvidenceListService service = new GetEvidenceListService(loadEvidencesByQuestion);

    private final Long QUESTION1_ID = 1L;
    private final Long QUESTION2_ID = 2L;
    private final String DESC = "desc";

    private final Evidence evidence1Q1 = new Evidence(UUID.randomUUID(), DESC, LocalDateTime.now(), LocalDateTime.now(), 1L, UUID.randomUUID(), QUESTION1_ID);
    private final Evidence evidence2Q1 = new Evidence(UUID.randomUUID(), DESC, LocalDateTime.now(), LocalDateTime.now(), 1L, UUID.randomUUID(), QUESTION1_ID);

    @Test
    void getEvidenceList_ResultsFound_2ItemsReturned() {
        when(loadEvidencesByQuestion.loadEvidencesByQuestionId(new LoadEvidencesByQuestionPort.Param(QUESTION1_ID), 0, 10)).thenReturn(new LoadEvidencesByQuestionPort.Result(List.of(evidence1Q1, evidence2Q1)));

        GetEvidenceListUseCase.Result result = service.getEvidenceList(new GetEvidenceListUseCase.Param(QUESTION1_ID, 10, 0));

        assertEquals(2, result.evidences().size());
    }

    @Test
    void getEvidenceList_ResultsFound_0ItemReturned() {
        when(loadEvidencesByQuestion.loadEvidencesByQuestionId(new LoadEvidencesByQuestionPort.Param(QUESTION2_ID), 0, 10)).thenReturn(new LoadEvidencesByQuestionPort.Result(new ArrayList<>()));

        GetEvidenceListUseCase.Result result = service.getEvidenceList(new GetEvidenceListUseCase.Param(QUESTION2_ID, 10, 0));

        assertEquals(0, result.evidences().size());
    }

    @Test
    void getEvidenceList_NullQuestion_ReturnErrorMessage() {
        assertThrows(ConstraintViolationException.class, () -> service.getEvidenceList(new GetEvidenceListUseCase.Param(null, 10, 0)));
    }
}
