package org.flickit.assessment.users.application.service.space;

import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.users.application.port.in.space.CheckCreateSpaceUseCase;
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
import static org.mockito.Mockito.*;

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
    void testCheckCreateSpace_whenUserBasicSpacesExceedsTheLimit_thenReturnFalse() {
        when(countSpacesPort.countBasicSpaces(param.getCurrentUserId()))
            .thenReturn(maxBasicSpaces + 1);

        var result = service.checkCreateSpace(param);
        assertFalse(result.allowCreateBasic());

        verify(appSpecProperties, times(1)).getSpace();
    }

    @Test
    void testCheckCreateSpace_whenUserBasicSpacesIsEqualToTheLimit_thenReturnFalse() {
        when(countSpacesPort.countBasicSpaces(param.getCurrentUserId()))
            .thenReturn(maxBasicSpaces);

        var result = service.checkCreateSpace(param);
        assertFalse(result.allowCreateBasic());

        verify(appSpecProperties, times(1)).getSpace();
    }

    @Test
    void testCheckCreateSpace_whenUserBasicSpacesIsLessThanTheLimit_thenReturnTrue() {
        when(countSpacesPort.countBasicSpaces(param.getCurrentUserId()))
            .thenReturn(maxBasicSpaces - 1);

        var result = service.checkCreateSpace(param);
        assertTrue(result.allowCreateBasic());

        verify(appSpecProperties, times(1)).getSpace();
    }

    private AppSpecProperties appSpecProperties() {
        var properties = new AppSpecProperties();
        properties.setSpace(new AppSpecProperties.Space());
        properties.getSpace().setMaxBasicSpaces(maxBasicSpaces);
        return properties;
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
