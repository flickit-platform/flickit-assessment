package org.flickit.assessment.kit.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserAccess {

    private final Long id;
    private final AssessmentKit assessmentKit;
    private final User user;
}
