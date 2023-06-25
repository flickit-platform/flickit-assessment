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
import static org.mockito.Mockito.doReturn;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class GetEvidenceListServiceTest {

    private final LoadEvidencesByQuestionPort loadEvidencesByQuestion = Mockito.mock(LoadEvidencesByQuestionPort.class);

    private final GetEvidenceListService service = new GetEvidenceListService(loadEvidencesByQuestion);

    private final Long QUESTION1_ID = 1L;
    private final Long QUESTION2_ID = 2L;
    private final Long QUESTION3_ID = 3L;
    private final String DESC = "desc";

    private final Evidence evidence1Q1 = new Evidence(UUID.randomUUID(), DESC, LocalDateTime.now(), LocalDateTime.now(), 1L, UUID.randomUUID(), QUESTION1_ID);
    private final Evidence evidence2Q1 = new Evidence(UUID.randomUUID(), DESC, LocalDateTime.now(), LocalDateTime.now(), 1L, UUID.randomUUID(), QUESTION1_ID);
    private final Evidence evidence1Q2 = new Evidence(UUID.randomUUID(), DESC, LocalDateTime.now(), LocalDateTime.now(), 1L, UUID.randomUUID(), QUESTION2_ID);

    @Test
    public void getAllEvidencesOfExistingQuestion1ReturnsIts2Evidences_success() {
        doReturn(new LoadEvidencesByQuestionPort.Result(List.of(evidence1Q1, evidence2Q1))).when(loadEvidencesByQuestion).loadEvidencesByQuestionId(new LoadEvidencesByQuestionPort.Param(QUESTION1_ID));

        GetEvidenceListUseCase.Result result = service.getEvidenceList(new GetEvidenceListUseCase.Param(QUESTION1_ID));

        assertEquals(2, result.evidences().size());
    }

    @Test
    public void getEvidencesOfNonExistingQuestionReturn0_success() {
        doReturn(new LoadEvidencesByQuestionPort.Result(new ArrayList<>())).when(loadEvidencesByQuestion).loadEvidencesByQuestionId(new LoadEvidencesByQuestionPort.Param(QUESTION3_ID));

        GetEvidenceListUseCase.Result result = service.getEvidenceList(new GetEvidenceListUseCase.Param(QUESTION3_ID));

        assertEquals(0, result.evidences().size());
    }

    @Test
    public void getAllEvidencedByNullQuestion_fail() {
        assertThrows(ConstraintViolationException.class, () -> service.getEvidenceList(new GetEvidenceListUseCase.Param(null)));
    }
}
