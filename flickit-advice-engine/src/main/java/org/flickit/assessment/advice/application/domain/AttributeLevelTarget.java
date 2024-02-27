package org.flickit.assessment.advice.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AttributeLevelTarget {

    private final long attributeId;
    private final long maturityLevelId;
}
