package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.port.in.assessmentkit.CreateAssessmentKitUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CreateAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAssessmentKitServiceTest {

    @InjectMocks
    private CreateAssessmentKitService service;

    @Mock
    private CheckExpertGroupAccessPort checkExpertGroupAccessPort;

    @Mock
    private CreateAssessmentKitPort createAssessmentKitPort;

    @Test
    void testCreateAssessmentKit_CurrentUserDoesNotHaveAccess_ShouldFailToCreateAssessmentKit() {
        var expertGroupId = 1L;
        var currentUserId = UUID.randomUUID();
        var param = new CreateAssessmentKitUseCase.Param("title", "summary", "about", true, expertGroupId, currentUserId);

        when(checkExpertGroupAccessPort.checkIsMember(expertGroupId, currentUserId)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.createAssessmentKit(param));

        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
        verify(checkExpertGroupAccessPort).checkIsMember(expertGroupId, currentUserId);
        verifyNoMoreInteractions(createAssessmentKitPort);
    }

    @Test
    void testCreateAssessmentKit_validParameters_CreateAssessmentKit() {
        var expertGroupId = 1L;
        var currentUserId = UUID.randomUUID();
        var kitId = 2L;
        var kitVersionId = 3L;
        var portResult = new CreateAssessmentKitPort.Result(kitId, kitVersionId);
        var param = new CreateAssessmentKitUseCase.Param("title", "summary", "about", true, expertGroupId, currentUserId);

        when(checkExpertGroupAccessPort.checkIsMember(expertGroupId, currentUserId)).thenReturn(true);
        when(createAssessmentKitPort.persist(any())).thenReturn(portResult);

        var result = assertDoesNotThrow(() -> service.createAssessmentKit(param));

        assertEquals(kitId, result.kitId());

        verify(checkExpertGroupAccessPort).checkIsMember(expertGroupId, currentUserId);
        verify(createAssessmentKitPort).persist(any());
    }

}
