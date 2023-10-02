package org.flickit.flickitassessmentcore.application.service.assessment;

import org.flickit.flickitassessmentcore.application.port.in.assessment.CountAssessmentsUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.CountAssessmentsPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CountAssessmentsServiceTest {

    @InjectMocks
    private CountAssessmentsService service;

    @Mock
    private CountAssessmentsPort countAssessmentsPort;

    @Test
    void testCountAssessments_TrueInputs_ValidResult() {
        var portParam = new CountAssessmentsPort.Param(1L, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE);
        var portResult = new CountAssessmentsPort.Result(2, 1, 1);
        when(countAssessmentsPort.countByKitId(portParam)).thenReturn(portResult);

        var param = new CountAssessmentsUseCase.Param(1L, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE);
        var result = service.countAssessments(param);

        assertEquals(2, result.totalCount());
        assertEquals(1, result.deletedCount());
        assertEquals(1, result.notDeletedCount());

        ArgumentCaptor<CountAssessmentsPort.Param> paramArgument = ArgumentCaptor.forClass(CountAssessmentsPort.Param.class);
        verify(countAssessmentsPort).countByKitId(paramArgument.capture());

        assertEquals(1L, paramArgument.getValue().assessmentKitId());
        assertEquals(Boolean.TRUE, paramArgument.getValue().deleted());
        assertEquals(Boolean.TRUE, paramArgument.getValue().notDeleted());
        assertEquals(Boolean.TRUE, paramArgument.getValue().total());
    }

    @Test
    void testCountAssessments_JustTrueNotDeleted_ValidResults() {
        var portParam = new CountAssessmentsPort.Param(1L, Boolean.FALSE, Boolean.TRUE, Boolean.FALSE);
        var portResult = new CountAssessmentsPort.Result(null, null, 1);
        when(countAssessmentsPort.countByKitId(portParam)).thenReturn(portResult);

        var param = new CountAssessmentsUseCase.Param(1L, Boolean.FALSE, Boolean.TRUE, Boolean.FALSE);
        var result = service.countAssessments(param);

        assertNull(result.totalCount());
        assertNull(result.deletedCount());
        assertEquals(1, result.notDeletedCount());
    }

    @Test
    void testCountAssessments_JustTrueDeleted_ValidResults() {
        var portParam = new CountAssessmentsPort.Param(1L, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE);
        var portResult = new CountAssessmentsPort.Result(null, 1, null);
        when(countAssessmentsPort.countByKitId(portParam)).thenReturn(portResult);

        var param = new CountAssessmentsUseCase.Param(1L, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE);
        var result = service.countAssessments(param);

        assertNull(result.totalCount());
        assertNull(result.notDeletedCount());
        assertEquals(1, result.deletedCount());
    }

    @Test
    void testCountAssessments_JustTrueTotal_ValidResults() {
        var portParam = new CountAssessmentsPort.Param(1L, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE);
        var portResult = new CountAssessmentsPort.Result(1, null, null);
        when(countAssessmentsPort.countByKitId(portParam)).thenReturn(portResult);

        var param = new CountAssessmentsUseCase.Param(1L, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE);
        var result = service.countAssessments(param);

        assertNull(result.deletedCount());
        assertNull(result.notDeletedCount());
        assertEquals(1, result.totalCount());
    }

}
