package org.flickit.flickitassessmentcore.application.service.assessment;

import org.flickit.flickitassessmentcore.application.port.in.assessment.RemoveAssessmentUseCase;
import org.flickit.flickitassessmentcore.application.port.out.SoftDeleteAssessmentPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import static org.flickit.flickitassessmentcore.application.service.assessment.CreateAssessmentService.NOT_DELETED_DELETION_TIME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RemoveAssessmentServiceTest {
    @Mock
    private SoftDeleteAssessmentPort deleteAssessmentPort;

    @InjectMocks
    private RemoveAssessmentService removeAssessmentService;

    @Test
    void testRemoveAssessment() {
        UUID id = UUID.randomUUID();
        RemoveAssessmentUseCase.Param param = new RemoveAssessmentUseCase.Param(id);
        doNothing().when(deleteAssessmentPort).setDeletionTimeById(any());

        removeAssessmentService.removeAssessment(param);

        ArgumentCaptor<SoftDeleteAssessmentPort.Param> portParam = ArgumentCaptor.forClass(SoftDeleteAssessmentPort.Param.class);
        verify(deleteAssessmentPort).setDeletionTimeById(portParam.capture());

        assertEquals(id, portParam.getValue().id());
        assertThat(portParam.getValue().deletionTime(), not(equalTo(NOT_DELETED_DELETION_TIME)));
        long now = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault()).toInstant().toEpochMilli();
        assertThat(portParam.getValue().deletionTime(), lessThanOrEqualTo(now));
        verify(deleteAssessmentPort, times(1)).setDeletionTimeById(any());
    }
}
