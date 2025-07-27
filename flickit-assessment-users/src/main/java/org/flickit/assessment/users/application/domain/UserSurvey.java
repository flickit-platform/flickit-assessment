package org.flickit.assessment.users.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSurvey {

    Long id;
    boolean answered;
    boolean dontShowAgain;
}
