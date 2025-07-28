package org.flickit.assessment.users.application.usersurvey;

import org.flickit.assessment.users.application.port.in.usersurvey.SetUserSurveyDoNotShowAgainUseCase;
import org.flickit.assessment.users.application.port.out.usersurvey.CreateUserSurveyPort;
import org.flickit.assessment.users.application.port.out.usersurvey.LoadUserSurveyPort;
import org.flickit.assessment.users.application.port.out.usersurvey.UpdateUserSurveyPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.users.test.fixture.application.UserSurveyMother.createSimpleUserSurvey;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SetUserSurveyDoNotShowAgainServiceTest {

    @InjectMocks
    private SetUserSurveyDoNotShowAgainService service;

    @Mock
    private LoadUserSurveyPort loadUserSurveyPort;

    @Mock
    private CreateUserSurveyPort createUserSurveyPort;

    @Mock
    private UpdateUserSurveyPort updateUserSurveyPort;

    private final SetUserSurveyDoNotShowAgainUseCase.Param param = createParam(SetUserSurveyDoNotShowAgainUseCase.Param.ParamBuilder::build);

    @Test
    void testSetUserSurveyDoNotShowAgain_whenUserSurveyDoesNotExist_thenCreateUserSurveyAndSetAsDontShowAgain() {
        when(loadUserSurveyPort.loadByUserId(param.getCurrentUserId())).thenReturn(Optional.empty());

        service.setDontShowAgain(param);

        var createParamCaptor = ArgumentCaptor.forClass(CreateUserSurveyPort.Param.class);
        verify(createUserSurveyPort).persist(createParamCaptor.capture());
        CreateUserSurveyPort.Param capturedParam = createParamCaptor.getValue();
        assertEquals(param.getCurrentUserId(), capturedParam.userId());
        assertEquals(param.getAssessmentId(), capturedParam.assessmentId());
        assertTrue(capturedParam.dontShowAgain());
        assertNotNull(capturedParam.currentDateTime());

        verifyNoInteractions(updateUserSurveyPort);
    }

    @Test
    void testSetUserSurveyDoNotShowAgain_whenUserSurveyExists_thenSetAsDontShowAgain() {
        var userSurvey = createSimpleUserSurvey();
        when(loadUserSurveyPort.loadByUserId(param.getCurrentUserId())).thenReturn(Optional.of(userSurvey));

        service.setDontShowAgain(param);

        var updateParamCaptor = ArgumentCaptor.forClass(UpdateUserSurveyPort.Param.class);
        verify(updateUserSurveyPort).updateDontShowAgain(updateParamCaptor.capture());
        UpdateUserSurveyPort.Param capturedParam = updateParamCaptor.getValue();
        assertEquals(param.getCurrentUserId(), capturedParam.userId());
        assertTrue(capturedParam.dontShowAgain());
        assertNotNull(capturedParam.lastModificationTime());

        verifyNoInteractions(createUserSurveyPort);
    }

    private SetUserSurveyDoNotShowAgainUseCase.Param createParam(Consumer<SetUserSurveyDoNotShowAgainUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private SetUserSurveyDoNotShowAgainUseCase.Param.ParamBuilder paramBuilder() {
        return SetUserSurveyDoNotShowAgainUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
