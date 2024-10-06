package org.flickit.assessment.kit.application.service.subject;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.in.subject.GetSubjectListUseCase;
import org.flickit.assessment.kit.application.port.in.subject.GetSubjectListUseCase.Param;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.flickit.assessment.kit.test.fixture.application.KitVersionMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_VERSION_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetSubjectListServiceTest {

    @InjectMocks
    GetSubjectListService service;

    @Mock
    LoadKitVersionPort loadKitVersionPort;

    @Mock
    CheckExpertGroupAccessPort checkExpertGroupAccessPort;

    @Test
    void testGetSubjectListService_kitVersionNotExist_shouldThrowResourceNotFoundException() {
        Param param = createParam(GetSubjectListUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenThrow(new ResourceNotFoundException(KIT_VERSION_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getSubjectList(param));
        assertEquals(KIT_VERSION_ID_NOT_FOUND, throwable.getMessage());

        verify(loadKitVersionPort).load(param.getKitVersionId());

        verifyNoInteractions(checkExpertGroupAccessPort);
    }

    @Test
    void testGetSubjectListService_CurrentUserIsNotExpertGroupMember_shouldThrowAccessDeniedException() {
        Param param = createParam(GetSubjectListUseCase.Param.ParamBuilder::build);
        var assessmentKit = AssessmentKitMother.simpleKit();
        var kitVersion = KitVersionMother.createKitVersion(assessmentKit);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId())).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getSubjectList(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verify(loadKitVersionPort).load(param.getKitVersionId());
        verify(checkExpertGroupAccessPort).checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId());
    }

    private Param createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
        return paramBuilder.build();
    }

    private GetSubjectListUseCase.Param.ParamBuilder paramBuilder() {
        return GetSubjectListUseCase.Param.builder()
            .kitVersionId(1L)
            .size(1)
            .page(10)
            .currentUserId(UUID.randomUUID());
    }
}
