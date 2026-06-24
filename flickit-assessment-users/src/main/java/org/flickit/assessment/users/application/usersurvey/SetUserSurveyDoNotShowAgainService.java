package org.flickit.assessment.users.application.usersurvey;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.users.application.port.in.usersurvey.SetUserSurveyDoNotShowAgainUseCase;
import org.flickit.assessment.users.application.port.out.usersurvey.CreateUserSurveyPort;
import org.flickit.assessment.users.application.port.out.usersurvey.LoadUserSurveyPort;
import org.flickit.assessment.users.application.port.out.usersurvey.UpdateUserSurveyPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class SetUserSurveyDoNotShowAgainService implements SetUserSurveyDoNotShowAgainUseCase {

    private final LoadUserSurveyPort loadUserSurveyPort;
    private final UpdateUserSurveyPort updateUserSurveyPort;
    private final CreateUserSurveyPort createUserSurveyPort;

    @Override
    public void setDontShowAgain(Param param) {
        if (loadUserSurveyPort.existsByUserId(param.getCurrentUserId()))
            updateUserSurveyPort.updateDontShowAgain(toUpdateParam(param.getCurrentUserId()));
        else
            createUserSurveyPort.persist(toCreateParam(param.getCurrentUserId(), param.getAssessmentId()));
    }

    private UpdateUserSurveyPort.Param toUpdateParam(UUID userId) {
        return new UpdateUserSurveyPort.Param(userId,
            true,
            LocalDateTime.now());
    }

    private CreateUserSurveyPort.Param toCreateParam(UUID userId, UUID assessmentId) {
        return new CreateUserSurveyPort.Param(userId,
            assessmentId,
            true,
            LocalDateTime.now());
    }
}
