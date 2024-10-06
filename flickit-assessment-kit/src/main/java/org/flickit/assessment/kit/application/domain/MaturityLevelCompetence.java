package org.flickit.assessment.kit.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MaturityLevelCompetence {

    private final long id;
    private final long effectiveLevelId;
    private final String effectiveLevelTitle;
    private final int value;
}
