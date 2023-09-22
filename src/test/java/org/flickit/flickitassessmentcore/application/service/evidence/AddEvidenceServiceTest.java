package org.flickit.flickitassessmentcore.application.service.evidence;

import org.flickit.flickitassessmentcore.application.port.in.evidence.AddEvidenceUseCase;
import org.flickit.flickitassessmentcore.application.port.out.evidence.CreateEvidencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddEvidenceServiceTest {

    @InjectMocks
    private AddEvidenceService service;

    @Mock
    private CreateEvidencePort createEvidencePort;

    @Test
    void testAddEvidence_ValidParam_PersistsAndReturnsId() {
        AddEvidenceUseCase.Param param = new AddEvidenceUseCase.Param(
            "desc",
            1L,
            UUID.randomUUID(),
            1L
        );
        UUID expectedId = UUID.randomUUID();
        when(createEvidencePort.persist(any(CreateEvidencePort.Param.class))).thenReturn(expectedId);

        AddEvidenceUseCase.Result result = service.addEvidence(param);
        assertNotNull(result.id());
        assertEquals(expectedId, result.id());

        ArgumentCaptor<CreateEvidencePort.Param> createPortParam = ArgumentCaptor.forClass(CreateEvidencePort.Param.class);
        verify(createEvidencePort).persist(createPortParam.capture());

        assertEquals(param.getDescription(), createPortParam.getValue().description());
        assertEquals(param.getCreatedById(), createPortParam.getValue().createdById());
        assertEquals(param.getAssessmentId(), createPortParam.getValue().assessmentId());
        assertEquals(param.getQuestionId(), createPortParam.getValue().questionId());
        assertNotNull(createPortParam.getValue().creationTime());
        assertNotNull(createPortParam.getValue().lastModificationTime());
    }
}
