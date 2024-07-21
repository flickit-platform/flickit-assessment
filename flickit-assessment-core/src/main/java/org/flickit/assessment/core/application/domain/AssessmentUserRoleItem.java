package org.flickit.assessment.core.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class AssessmentUserRoleItem {

    private final UUID assessmentId;
    private final UUID userId;
    private final Integer roleId;
}
