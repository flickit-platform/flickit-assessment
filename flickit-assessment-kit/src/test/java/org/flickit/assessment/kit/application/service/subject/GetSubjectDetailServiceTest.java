package org.flickit.assessment.kit.application.service.subject;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.in.subject.GetSubjectDetailUseCase;
import org.flickit.assessment.kit.application.port.in.subject.GetSubjectDetailUseCase.Param;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.subject.LoadSubjectDetailPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetSubjectDetailServiceTest {

    @InjectMocks
    private GetSubjectDetailService service;

    @Mock
    private LoadKitExpertGroupPort loadKitExpertGroupPort;

    @Mock
    private CheckExpertGroupAccessPort checkExpertGroupAccessPort;

    @Mock
    private LoadSubjectDetailPort loadSubjectDetailPort;


    @Test
    void testGetSubjectDetail_WhenKitExist_shouldReturnKitDetails() {
        Param param = new Param(2000L, 2L, UUID.randomUUID());
        var expertGroupId = 14L;

        when(loadKitExpertGroupPort.loadKitExpertGroupId(param.getKitId())).thenReturn(expertGroupId);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroupId, param.getCurrentUserId())).thenReturn(true);

        GetSubjectDetailUseCase.Result expectedResult = new GetSubjectDetailUseCase.Result(
            123,
            "subject description",
            List.of(
                new GetSubjectDetailUseCase.Attribute(1L, 1, "attribute1"),
                new GetSubjectDetailUseCase.Attribute(2L, 2, "attribute2"),
                new GetSubjectDetailUseCase.Attribute(3L, 3, "attribute3")
            )
        );

        when(loadSubjectDetailPort.loadByIdAndKitId(param.getSubjectId(), param.getKitId())).thenReturn(expectedResult);

        GetSubjectDetailUseCase.Result result = service.getSubjectDetail(param);

        assertEquals(expectedResult.questionCount(), result.questionCount());
        assertEquals(expectedResult.description(), result.description());
        assertIterableEquals(expectedResult.attributes(), result.attributes());
    }

    @Test
    void testGetSubjectDetail_WhenKitDoesNotExist_ThrowsException() {
        Param param = new Param(2000L, 2L, UUID.randomUUID());

        when(loadKitExpertGroupPort.loadKitExpertGroupId(param.getKitId()))
            .thenThrow(new ResourceNotFoundException(KIT_ID_NOT_FOUND));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.getSubjectDetail(param));
        assertEquals(KIT_ID_NOT_FOUND, exception.getMessage());
    }

    @Test
    void testGetSubjectDetail_WhenUserIsNotMember_ThrowsException() {
        Param param = new Param(2000L, 2L, UUID.randomUUID());
        var expertGroupId = 14L;

        when(loadKitExpertGroupPort.loadKitExpertGroupId(param.getKitId())).thenReturn(expertGroupId);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroupId, param.getCurrentUserId())).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> service.getSubjectDetail(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());
    }
}
