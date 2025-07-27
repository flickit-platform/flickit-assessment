package org.flickit.assessment.users.adapter.out.persistence.usersurvey;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.users.usersurvey.UserSurveyJpaEntity;
import org.flickit.assessment.users.application.domain.UserSurvey;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSurveyMapper {

    public static UserSurvey mapToDomain(UserSurveyJpaEntity entity) {
        return new UserSurvey(entity.getId(),
            entity.isCompleted(),
            entity.isDontShowAgain());
    }
}
