package org.flickit.assessment.users.application.service.space;

import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.users.application.port.out.space.CheckCreateSpaceUseCase;
import org.flickit.assessment.users.application.port.out.space.CountSpacesPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckCreateSpaceServiceTest {

    @InjectMocks
    CheckCreateSpaceService service;

    @Mock
    CountSpacesPort countSpacesPort;

    @Spy
    AppSpecProperties appSpecProperties = appSpecProperties();

    private final CheckCreateSpaceUseCase.Param param = createParam(CheckCreateSpaceUseCase.Param.ParamBuilder::build);
    private final int maxBasicSpaces = 2;

    @Test
    public void testCheckCreateSpace_WhenUserPrivateSpacesExceedsTheLimit_ThenReturnFalse() {
        when(countSpacesPort.countBasicSpaces(param.getCurrentUserId()))
            .thenReturn(maxBasicSpaces + 1);

        var result = service.checkCreateSpace(param);
        assertFalse(result.allowCreateBasic());
    }

    @Test
    public void testCheckCreateSpace_WhenUserPrivateSpacesEqualsToLimit_ThenReturnFalse() {
        when(countSpacesPort.countBasicSpaces(param.getCurrentUserId()))
            .thenReturn(maxBasicSpaces);

        var result = service.checkCreateSpace(param);
        assertFalse(result.allowCreateBasic());
    }

    @Test
    public void testCheckCreateSpace_WhenUserPrivateSpacesIsLessThanLimit_ThenReturnFalse() {
        when(countSpacesPort.countBasicSpaces(param.getCurrentUserId()))
            .thenReturn(maxBasicSpaces - 1);

        var result = service.checkCreateSpace(param);
        assertTrue(result.allowCreateBasic());
    }

    private AppSpecProperties appSpecProperties() {
        var appSpecProperties = new AppSpecProperties();
        var space = new AppSpecProperties().getSpace();
        space.setMaxBasicSpaces(maxBasicSpaces);
        appSpecProperties.setSpace(space);
        return appSpecProperties;
    }

    private CheckCreateSpaceUseCase.Param createParam(Consumer<CheckCreateSpaceUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private CheckCreateSpaceUseCase.Param.ParamBuilder paramBuilder() {
        return CheckCreateSpaceUseCase.Param.builder()
            .currentUserId(UUID.randomUUID());
    }
}
