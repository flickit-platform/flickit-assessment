package org.flickit.assessment.common.application.domain.advice;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AttributeLevelTarget {

    private final long attributeId;
    private final long maturityLevelId;
}
