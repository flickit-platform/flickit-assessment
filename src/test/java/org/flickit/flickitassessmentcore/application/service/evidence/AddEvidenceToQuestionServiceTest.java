package org.flickit.flickitassessmentcore.application.service.evidence;

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
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class AddEvidenceToQuestionServiceTest {

    private final CreateEvidencePort createEvidence = Mockito.mock(CreateEvidencePort.class);

    private final AddEvidenceToQuestionService service = new AddEvidenceToQuestionService(
        createEvidence
    );

    private Evidence evidence = new Evidence(
        UUID.randomUUID(),
        "desc",
        LocalDateTime.now(),
        LocalDateTime.now(),
        1L,
        UUID.randomUUID(),
        1L
    );

    @Test
    public void createAnEvidenceAndSaveIt_success() {
        doReturn(new CreateEvidencePort.Result(evidence)).when(createEvidence).createEvidence(new CreateEvidencePort.Param(evidence));

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

    }

    @Test
    public void createAnEvidenceWithNullCreatedById_fail() {

    }

    @Test
    public void createAnEvidenceWithNullAssessmentId_fail() {

    }

    @Test
    public void createAnEvidenceWithNullQuestionId_fail() {

    }
}
