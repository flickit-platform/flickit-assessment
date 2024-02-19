package org.flickit.assessment.core.application.service.evidence;

import org.flickit.assessment.core.application.domain.EvidenceType;
import org.flickit.assessment.core.application.port.in.evidence.UpdateEvidenceUseCase;
import org.flickit.assessment.core.application.port.out.evidence.UpdateEvidencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.flickit.assessment.core.test.fixture.application.EvidenceMother.simpleEvidence;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateEvidenceServiceTest {

    @InjectMocks
    private UpdateEvidenceService service;

    @Mock
    private UpdateEvidencePort updateEvidencePort;

    @Test
    void testUpdateEvidence_ValidParam_UpdatedAndReturnsId() {
        var savedEvidence = simpleEvidence();
        String code = EvidenceType.values()[savedEvidence.getType()].getCode();
        var param = new UpdateEvidenceUseCase.Param(
            savedEvidence.getId(),
            "new " + savedEvidence.getDescription(),
            code,
            savedEvidence.getLastModifiedById()
        );

        var updateResult = new UpdateEvidencePort.Result(savedEvidence.getId());
        when(updateEvidencePort.update(any())).thenReturn(updateResult);

        UpdateEvidenceUseCase.Result result = service.updateEvidence(param);

        assertEquals(savedEvidence.getId(), result.id());

        ArgumentCaptor<UpdateEvidencePort.Param> updateParamArgumentCaptor = ArgumentCaptor.forClass(UpdateEvidencePort.Param.class);
        verify(updateEvidencePort).update(updateParamArgumentCaptor.capture());

        assertEquals(param.getId(), updateParamArgumentCaptor.getValue().id());
        assertEquals(param.getDescription(), updateParamArgumentCaptor.getValue().description());
        assertNotEquals(savedEvidence.getLastModificationTime(), updateParamArgumentCaptor.getValue().lastModificationTime());
    }
}
