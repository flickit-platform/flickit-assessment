package org.flickit.assessment.users.application.usersurvey;

import org.flickit.assessment.users.application.port.in.usersurvey.SetUserSurveyDoNotShowAgainUseCase;
import org.flickit.assessment.users.application.port.out.usersurvey.CreateUserSurveyPort;
import org.flickit.assessment.users.application.port.out.usersurvey.LoadUserSurveyPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SetUserSurveyDoNotShowAgainServiceTest {

    @InjectMocks
    private SetUserSurveyDoNotShowAgainService service;

    @Mock
    private LoadUserSurveyPort loadUserSurveyPort;

    @Mock
    private CreateUserSurveyPort createUserSurveyPort;

    @Test
    void testSetUserSurveyDoNotShowAgain_whenUserSurveyDoesNotExist_thenCreateUserSurveyAndSetAsDontShowAgain() {


    }

    @Test
    void testSetUserSurveyDoNotShowAgain_whenUserSurveyExists_thenSetAsDontShowAgain() {


    }
}
