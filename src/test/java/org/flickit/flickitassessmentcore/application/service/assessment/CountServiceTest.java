package org.flickit.flickitassessmentcore.application.service.assessment;

import org.flickit.flickitassessmentcore.application.port.in.assessment.CountUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.CountAssessmentsByKitPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CountServiceTest {

    @InjectMocks
    private CountService service;

    @Mock
    private CountAssessmentsByKitPort countAssessmentsByKitPort;

    @Test
    void countAssessmentOnKit_TrueInputs_ValidResult() {
        when(countAssessmentsByKitPort.count(1L, Boolean.TRUE, Boolean.TRUE)).thenReturn(2);

        var param = new CountUseCase.Param(1L, Boolean.TRUE, Boolean.TRUE);
        var result = service.count(param);

        assertEquals(2, result.count());

        ArgumentCaptor<Long> kitIdArgument = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Boolean> includeDeletedArgument = ArgumentCaptor.forClass(Boolean.class);
        ArgumentCaptor<Boolean> includeNotDeletedArgument = ArgumentCaptor.forClass(Boolean.class);
        verify(countAssessmentsByKitPort).count(
            kitIdArgument.capture(), includeDeletedArgument.capture(), includeNotDeletedArgument.capture()
        );

        assertEquals(1L, kitIdArgument.getValue());
        assertEquals(Boolean.TRUE, includeDeletedArgument.getValue());
        assertEquals(Boolean.TRUE, includeNotDeletedArgument.getValue());
    }

    @Test
    void countAssessmentOnKit_JustTrueNotDeleted_ValidResults() {
        when(countAssessmentsByKitPort.count(1L, Boolean.FALSE, Boolean.TRUE)).thenReturn(2);

        var param = new CountUseCase.Param(1L, Boolean.FALSE, Boolean.TRUE);
        var result = service.count(param);

        assertEquals(2, result.count());
    }

    @Test
    void countAssessmentOnKit_JustTrueDeleted_ValidResults() {
        when(countAssessmentsByKitPort.count(1L, Boolean.TRUE, Boolean.FALSE)).thenReturn(2);

        var param = new CountUseCase.Param(1L, Boolean.TRUE, Boolean.FALSE);
        var result = service.count(param);

        assertEquals(2, result.count());
    }

}
