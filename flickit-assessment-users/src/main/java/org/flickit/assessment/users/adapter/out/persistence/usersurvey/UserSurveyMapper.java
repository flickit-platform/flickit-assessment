package org.flickit.assessment.users.adapter.out.persistence.usersurvey;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.users.usersurvey.UserSurveyJpaEntity;
import org.flickit.assessment.users.application.domain.UserSurvey;
import org.flickit.assessment.users.application.port.out.usersurvey.CreateUserSurveyPort;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSurveyMapper {

    public static UserSurvey mapToDomain(UserSurveyJpaEntity entity) {
        return new UserSurvey(entity.getId(),
                entity.isCompleted(),
                entity.isDontShowAgain());
    }

    public static UserSurveyJpaEntity mapCreateParamToJpaEntity(CreateUserSurveyPort.Param param) {
        return new UserSurveyJpaEntity(null,
                param.userId(),
                param.assessmentId(),
                false,
                false,
                null,
                param.currentDateTime(),
                param.currentDateTime());
    }
}
