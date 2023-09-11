package org.flickit.flickitassessmentcore.application.service.evidence;

import jakarta.validation.ConstraintViolationException;
import org.flickit.flickitassessmentcore.application.port.in.evidence.AddEvidenceUseCase;
import org.flickit.flickitassessmentcore.application.port.out.evidence.CreateEvidencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AddEvidenceServiceTest {

    @InjectMocks
    private AddEvidenceService service;

    @Mock
    private CreateEvidencePort createEvidencePort;

    @Test
    void addEvidence_ValidParam_PersistsAndReturnsId() {
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

    @Test
    void addEvidence_EmptyDesc_ReturnsErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new AddEvidenceUseCase.Param(
            "",
            1L,
            UUID.randomUUID(),
            1L
        ));
        assertThat(throwable).hasMessageContaining("description: " + ADD_EVIDENCE_DESC_NOT_BLANK)
            .hasMessageContaining("description: " + ADD_EVIDENCE_DESC_SIZE_MIN);
    }

    @Test
    void addEvidence_NullCreatedById_ReturnsErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class, () -> new AddEvidenceUseCase.Param(
            "desc",
            null,
            UUID.randomUUID(),
            1L
        ));
        assertThat(throwable).hasMessage("createdById: " + ADD_EVIDENCE_CREATED_BY_ID_NOT_NULL);
    }

    @Test
    void addEvidence_NullAssessmentId_ReturnsErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class, () -> new AddEvidenceUseCase.Param(
            "desc",
            1L,
            null,
            1L
        ));
        assertThat(throwable).hasMessage("assessmentId: " + ADD_EVIDENCE_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void addEvidence_NullQuestionId_ReturnsErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class, () -> new AddEvidenceUseCase.Param(
            "desc",
            1L,
            UUID.randomUUID(),
            null
        ));
        assertThat(throwable).hasMessage("questionId: " + ADD_EVIDENCE_QUESTION_ID_NOT_NULL);
    }
}
