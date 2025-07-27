package org.flickit.assessment.users.application.usersurvey;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.users.application.port.in.usersurvey.InitUserSurveyUseCase;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class InitUserSurveyService implements InitUserSurveyUseCase {

    @Override
    public Result initUserSurvey(Param param) {
        return null;
    }
}
