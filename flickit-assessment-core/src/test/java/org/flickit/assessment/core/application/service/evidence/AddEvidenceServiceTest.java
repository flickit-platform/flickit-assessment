package org.flickit.assessment.core.application.service.evidence;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.evidence.AddEvidenceUseCase;
import org.flickit.assessment.core.application.port.out.evidence.CreateEvidencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.ADD_EVIDENCE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddEvidenceServiceTest {

    @InjectMocks
    private AddEvidenceService service;

    @Mock
    private CreateEvidencePort createEvidencePort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Test
    void testAddEvidence_ValidParam_PersistsAndReturnsId() {
        AddEvidenceUseCase.Param param = new AddEvidenceUseCase.Param(
            "desc",
            UUID.randomUUID(),
            1L,
            "POSITIVE",
            UUID.randomUUID()
        );
        UUID expectedId = UUID.randomUUID();
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCreatedById(), ADD_EVIDENCE)).thenReturn(true);
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
    void testAddEvidence_InvalidCurrentUserId_ThrowDeniedAccessException() {
        AddEvidenceUseCase.Param param = new AddEvidenceUseCase.Param(
            "desc",
            UUID.randomUUID(),
            1L,
            "POSITIVE",
            UUID.randomUUID()
        );

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCreatedById(), ADD_EVIDENCE)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.addEvidence(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }
}
