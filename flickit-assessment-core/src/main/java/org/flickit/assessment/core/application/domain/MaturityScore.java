package org.flickit.assessment.core.application.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MaturityScore {

    @EqualsAndHashCode.Include
    private final long maturityLevelId;

    private final Double score;
}
