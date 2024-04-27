package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetExpertGroupKitListUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadExpertGroupAllKitListPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadExpertGroupPublishedKitListPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetExpertGroupKitListServiceTest {

    @InjectMocks
    private GetExpertGroupKitListService service;

    @Mock
    private CheckExpertGroupAccessPort checkExpertGroupAccessPort;

    @Mock
    private LoadExpertGroupAllKitListPort loadExpertGroupAllKitListPort;

    @Mock
    private LoadExpertGroupPublishedKitListPort loadExpertGroupPublishedKitListPort;

    @Test
    void testGetExpertGroupKitList_UserIsExpertGroupMember_ValidResult() {
        AssessmentKit expectedKit = AssessmentKitMother.simpleKit();
        GetExpertGroupKitListUseCase.Param param = new GetExpertGroupKitListUseCase.Param(expectedKit.getExpertGroupId(), 1, 10, UUID.randomUUID());
        PaginatedResponse<AssessmentKit> expectedPage = new PaginatedResponse<>(
            List.of(expectedKit),
            1,
            10,
            "title",
            "desc",
            1
        );
        when(checkExpertGroupAccessPort.checkIsMember(param.getExpertGroupId(), param.getCurrentUserId()))
            .thenReturn(true);
        when(loadExpertGroupAllKitListPort.loadAllKitsByKitIdAndUserId(param.getExpertGroupId(), param.getCurrentUserId(), param.getPage(), param.getSize()))
            .thenReturn(expectedPage);

        var serviceResult = service.getExpertGroupKitList(param);

        assertEquals(expectedKit.getId(), serviceResult.getItems().get(0).id());
        assertEquals(expectedPage.getPage(), serviceResult.getPage());
        assertEquals(expectedPage.getSize(), serviceResult.getSize());
        assertEquals(expectedPage.getSort(), serviceResult.getSort());
        assertEquals(expectedPage.getOrder(), serviceResult.getOrder());
        assertEquals(expectedPage.getTotal(), serviceResult.getTotal());

        verifyNoInteractions(loadExpertGroupPublishedKitListPort);
    }

    @Test
    void testGetExpertGroupKitList_UserIsNotExpertGroupMember_ValidResult() {
        AssessmentKit expectedKit = AssessmentKitMother.simpleKit();
        GetExpertGroupKitListUseCase.Param param = new GetExpertGroupKitListUseCase.Param(expectedKit.getExpertGroupId(), 1, 10, UUID.randomUUID());
        PaginatedResponse<AssessmentKit> expectedPage = new PaginatedResponse<>(
            List.of(expectedKit),
            1,
            10,
            "title",
            "desc",
            1
        );
        when(checkExpertGroupAccessPort.checkIsMember(param.getExpertGroupId(), param.getCurrentUserId()))
            .thenReturn(false);
        when(loadExpertGroupPublishedKitListPort.loadPublishedKitsByKitIdAndUserId(param.getExpertGroupId(), param.getCurrentUserId(), param.getPage(), param.getSize()))
            .thenReturn(expectedPage);

        var serviceResult = service.getExpertGroupKitList(param);

        assertEquals(expectedKit.getId(), serviceResult.getItems().get(0).id());
        assertEquals(expectedPage.getPage(), serviceResult.getPage());
        assertEquals(expectedPage.getSize(), serviceResult.getSize());
        assertEquals(expectedPage.getSort(), serviceResult.getSort());
        assertEquals(expectedPage.getOrder(), serviceResult.getOrder());
        assertEquals(expectedPage.getTotal(), serviceResult.getTotal());

        verifyNoInteractions(loadExpertGroupAllKitListPort);
    }
}
