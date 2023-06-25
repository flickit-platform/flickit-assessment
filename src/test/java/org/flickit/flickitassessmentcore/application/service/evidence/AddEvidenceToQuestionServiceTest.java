package org.flickit.flickitassessmentcore.application.service.evidence;

import jakarta.validation.ConstraintViolationException;
import org.flickit.flickitassessmentcore.application.port.in.evidence.AddEvidenceToQuestionUseCase;
import org.flickit.flickitassessmentcore.application.port.out.evidence.CreateEvidencePort;
import org.flickit.flickitassessmentcore.domain.Evidence;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class AddEvidenceToQuestionServiceTest {

    public static final String DESC = "desc";
    private final CreateEvidencePort createEvidence = Mockito.mock(CreateEvidencePort.class);

    private final AddEvidenceToQuestionService service = new AddEvidenceToQuestionService(
        createEvidence
    );

    private final Evidence evidence = new Evidence(
        null,
        DESC,
        LocalDateTime.now(),
        LocalDateTime.now(),
        1L,
        UUID.randomUUID(),
        1L
    );

    @Test
    public void createAnEvidenceAndSaveIt_success() {
        doReturn(new CreateEvidencePort.Result(evidence)).when(createEvidence).createEvidence(any(CreateEvidencePort.Param.class));

        AddEvidenceToQuestionUseCase.Result result = service.addEvidenceToQuestion(new AddEvidenceToQuestionUseCase.Param(
            evidence.getDescription(),
            evidence.getCreatedById(),
            evidence.getAssessmentId(),
            evidence.getQuestionId()
        ));

        assertNotNull(result.evidence());
    }

    @Test
    public void createAnEvidenceWithEmptyDesc_fail() {
        evidence.setDescription("");
        doReturn(new CreateEvidencePort.Result(evidence)).when(createEvidence).createEvidence(any(CreateEvidencePort.Param.class));

        assertThrows(ConstraintViolationException.class, () -> service.addEvidenceToQuestion(new AddEvidenceToQuestionUseCase.Param(
            evidence.getDescription(),
            evidence.getCreatedById(),
            evidence.getAssessmentId(),
            evidence.getQuestionId()
        )));

        evidence.setDescription(DESC);
    }

    @Test
    public void createAnEvidenceWithNullCreatedById_fail() {
        evidence.setCreatedById(null);
        doReturn(new CreateEvidencePort.Result(evidence)).when(createEvidence).createEvidence(any(CreateEvidencePort.Param.class));

        assertThrows(ConstraintViolationException.class, () -> service.addEvidenceToQuestion(new AddEvidenceToQuestionUseCase.Param(
            evidence.getDescription(),
            evidence.getCreatedById(),
            evidence.getAssessmentId(),
            evidence.getQuestionId()
        )));

        evidence.setCreatedById(1L);
    }

    @Test
    public void createAnEvidenceWithNullAssessmentId_fail() {
        evidence.setAssessmentId(null);
        doReturn(new CreateEvidencePort.Result(evidence)).when(createEvidence).createEvidence(any(CreateEvidencePort.Param.class));

        assertThrows(ConstraintViolationException.class, () -> service.addEvidenceToQuestion(new AddEvidenceToQuestionUseCase.Param(
            evidence.getDescription(),
            evidence.getCreatedById(),
            evidence.getAssessmentId(),
            evidence.getQuestionId()
        )));

        evidence.setAssessmentId(UUID.randomUUID());
    }

    @Test
    public void createAnEvidenceWithNullQuestionId_fail() {
        evidence.setQuestionId(null);
        doReturn(new CreateEvidencePort.Result(evidence)).when(createEvidence).createEvidence(any(CreateEvidencePort.Param.class));

        assertThrows(ConstraintViolationException.class, () -> service.addEvidenceToQuestion(new AddEvidenceToQuestionUseCase.Param(
            evidence.getDescription(),
            evidence.getCreatedById(),
            evidence.getAssessmentId(),
            evidence.getQuestionId()
        )));

        evidence.setQuestionId(1L);
    }
}
