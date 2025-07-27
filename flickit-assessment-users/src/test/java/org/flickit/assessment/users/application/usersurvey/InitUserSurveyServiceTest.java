package org.flickit.assessment.users.application.usersurvey;

import org.flickit.assessment.common.config.SurveyProperties;
import org.flickit.assessment.users.application.port.in.usersurvey.InitUserSurveyUseCase;
import org.flickit.assessment.users.application.port.out.usersurvey.CreateUserSurveyPort;
import org.flickit.assessment.users.application.port.out.usersurvey.LoadUserSurveyPort;
import org.flickit.assessment.users.test.fixture.application.UserSurveyMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InitUserSurveyServiceTest {

    @InjectMocks
    private InitUserSurveyService service;

    @Mock
    private LoadUserSurveyPort loadUserSurveyPort;

    @Mock
    private CreateUserSurveyPort createUserSurveyPort;

    @Spy
    private SurveyProperties surveyProperties = surveyProperties();

    @Captor
    private ArgumentCaptor<CreateUserSurveyPort.Param> paramCaptor;

    private final InitUserSurveyUseCase.Param param = createParam(InitUserSurveyUseCase.Param.ParamBuilder::build);

    @Test
    void testInitUserSurvey_whenUserSurveyExists_thenReturnUserSurvey() {
        var survey = UserSurveyMother.simpleUserSurvey();

        when(loadUserSurveyPort.loadByUserId(param.getCurrentUserId())).thenReturn(Optional.of(survey));

        var result = service.initUserSurvey(param);

        assertEquals(survey.getId(), result.userSurveyId());
        assertEquals(surveyProperties.getBaseUrl(), result.redirectUrl());

        verifyNoInteractions(createUserSurveyPort);
    }

    @Test
    void testInitUserSurvey_whenUserSurveyDoesNotExist_thenCreateUserSurvey() {
        long surveyId = 123L;

        when(loadUserSurveyPort.loadByUserId(param.getCurrentUserId())).thenReturn(Optional.empty());
        when(createUserSurveyPort.persist(paramCaptor.capture())).thenReturn(surveyId);

        var result = service.initUserSurvey(param);

        var createParam = paramCaptor.getValue();
        assertEquals(param.getAssessmentId(), createParam.assessmentId());
        assertEquals(param.getCurrentUserId(), createParam.userId());
        assertNotNull(createParam.currentDateTime());

        assertEquals(surveyId, result.userSurveyId());
        assertEquals(surveyProperties.getBaseUrl(), result.redirectUrl());
    }

    private SurveyProperties surveyProperties() {
        var properties = new SurveyProperties();
        properties.setBaseUrl("http://sample:9999");
        return properties;
    }

    private InitUserSurveyUseCase.Param createParam(Consumer<InitUserSurveyUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private InitUserSurveyUseCase.Param.ParamBuilder paramBuilder() {
        return InitUserSurveyUseCase.Param.builder()
                .assessmentId(UUID.randomUUID())
                .currentUserId(UUID.randomUUID());
    }
}
