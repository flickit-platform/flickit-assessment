package org.flickit.assessment.common.application.domain.assessment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {

    GRANT_ASSESSMENT_ROLE("grant-assessment-role");

    private final String code;
}
