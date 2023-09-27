package org.flickit.flickitassessmentcore.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LevelCompetence {

    private final long id;
    private final int value;
    private final long maturityLevelId;
}
