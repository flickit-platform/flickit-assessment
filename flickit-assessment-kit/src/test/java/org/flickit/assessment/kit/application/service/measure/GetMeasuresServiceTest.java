package org.flickit.assessment.kit.application.service.measure;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.measure.GetMeasuresUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.flickit.assessment.kit.test.fixture.application.KitVersionMother.createKitVersion;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetMeasuresServiceTest {

    @InjectMocks
    GetMeasuresService service;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    CheckExpertGroupAccessPort checkExpertGroupAccessPort;

    private final GetMeasuresUseCase.Param param = createParam(GetMeasuresUseCase.Param.ParamBuilder::build);
    private final KitVersion kitVersion = createKitVersion(simpleKit());

    @Test
    void getMeasures_whenCurrentUserIsNotMemberOfExpertGroup_thenThrowAccessDeniedException() {
        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId()))
            .thenReturn(false);

        AccessDeniedException throwable = assertThrows(AccessDeniedException.class, () -> service.getMeasures(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    private GetMeasuresUseCase.Param createParam(Consumer<GetMeasuresUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetMeasuresUseCase.Param.ParamBuilder paramBuilder() {
        return GetMeasuresUseCase.Param.builder()
            .kitVersionId(1L)
            .currentUserId(UUID.randomUUID())
            .page(1)
            .size(10);
    }
}
