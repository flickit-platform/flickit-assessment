package org.flickit.assessment.users.application.usersurvey;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.SurveyProperties;
import org.flickit.assessment.users.application.port.in.usersurvey.InitUserSurveyUseCase;
import org.flickit.assessment.users.application.port.out.usersurvey.CreateUserSurveyPort;
import org.flickit.assessment.users.application.port.out.usersurvey.LoadUserSurveyPort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class InitUserSurveyService implements InitUserSurveyUseCase {

    private final LoadUserSurveyPort loadUserSurveyPort;
    private final SurveyProperties surveyProperties;
    private final CreateUserSurveyPort createUserSurveyPort;

    @Override
    public Result initUserSurvey(Param param) {
        String baseUrl = surveyProperties.getBaseUrl();
        return loadUserSurveyPort.loadByUserId(param.getCurrentUserId())
            .map(userSurvey -> new Result(userSurvey.getId(), baseUrl))
            .orElseGet(() -> {
                var surveyId = createUserSurveyPort.persist(
                    toParam(param.getCurrentUserId(), param.getAssessmentId())
                );
                return new Result(surveyId, baseUrl);
            });
    }

    private CreateUserSurveyPort.Param toParam(UUID userId, UUID assessmentId) {
        return new CreateUserSurveyPort.Param(
            userId,
            assessmentId,
            LocalDateTime.now());
    }
}
