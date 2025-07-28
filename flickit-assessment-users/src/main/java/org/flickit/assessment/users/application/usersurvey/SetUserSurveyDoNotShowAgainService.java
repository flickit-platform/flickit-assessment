package org.flickit.assessment.users.application.usersurvey;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.users.application.port.in.usersurvey.SetUserSurveyDoNotShowAgainUseCase;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class SetUserSurveyDoNotShowAgainService implements SetUserSurveyDoNotShowAgainUseCase {

    @Override
    public void setDontShowAgain(UUID userId) {

    }
}
