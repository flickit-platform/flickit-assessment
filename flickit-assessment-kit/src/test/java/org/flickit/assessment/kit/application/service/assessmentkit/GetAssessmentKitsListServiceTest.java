package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetAssessmentKitsListUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitsListPort;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.UUID;


@ExtendWith(MockitoExtension.class)
class GetAssessmentKitsListServiceTest {

    @InjectMocks
    private GetAssessmentKitsListService service;
    @Mock
    private LoadAssessmentKitsListPort port;

    @Test
    void testGetAssessmentKitsList_ParamAsInput_PaginatedResponseASOutput() {
        UUID currentUserId = UUID.randomUUID();
        LoadAssessmentKitsListPort.Param portParam =
            new LoadAssessmentKitsListPort.Param(true, 1, 10, currentUserId);
        GetAssessmentKitsListUseCase.Param  useCaseParam =
            new GetAssessmentKitsListUseCase.Param(true, 1, 10, currentUserId);
        PaginatedResponse<GetAssessmentKitsListUseCase.KitsListItem> response =
            new PaginatedResponse<>(new ArrayList<>(), 0, 20, "Title", "Desc", 100);
        Mockito.when(port.loadKitsList(portParam)).thenReturn(response);

        PaginatedResponse<GetAssessmentKitsListUseCase.KitsListItem> result = service.getKitsList(useCaseParam);

        Assertions.assertEquals(response, result);
        Mockito.verify(port, Mockito.times(1)).loadKitsList(portParam);
    }
}
