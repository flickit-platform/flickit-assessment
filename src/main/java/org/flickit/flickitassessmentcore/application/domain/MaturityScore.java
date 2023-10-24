package org.flickit.flickitassessmentcore.application.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MaturityScore {

    @EqualsAndHashCode.Include
    private final Long maturityLevelId;

    private final Double score;
}
