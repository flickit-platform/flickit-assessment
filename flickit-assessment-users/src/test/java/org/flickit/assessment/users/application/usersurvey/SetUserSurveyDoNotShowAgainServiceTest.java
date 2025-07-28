package org.flickit.assessment.users.application.usersurvey;

import org.flickit.assessment.users.application.port.in.usersurvey.SetUserSurveyDoNotShowAgainUseCase;
import org.flickit.assessment.users.application.port.out.usersurvey.CreateUserSurveyPort;
import org.flickit.assessment.users.application.port.out.usersurvey.LoadUserSurveyPort;
import org.flickit.assessment.users.application.port.out.usersurvey.UpdateUserSurveyPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.users.test.fixture.application.UserSurveyMother.createSimpleUserSurvey;
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
        when(loadUserSurveyPort.loadByUserId(param.getCurrentUserId())).thenReturn(Optional.of(createSimpleUserSurvey()));

        service.setDontShowAgain(param);

        verify(updateUserSurveyPort).updateDontShowAgain(param.getCurrentUserId(), true);
        verifyNoInteractions(createUserSurveyPort);
    }

    @Test
    void testSetUserSurveyDoNotShowAgain_whenUserSurveyExists_thenSetAsDontShowAgain() {
        var userSurvey = createSimpleUserSurvey();
        when(loadUserSurveyPort.loadByUserId(param.getCurrentUserId())).thenReturn(Optional.of(userSurvey));

        service.setDontShowAgain(param);

        verify(updateUserSurveyPort).updateDontShowAgain(param.getCurrentUserId(), true);
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
