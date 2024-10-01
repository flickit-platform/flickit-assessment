package org.flickit.assessment.kit.application.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class MaturityLevelOrder {
    private final Long id;
    private final Integer index;
    private final Integer value;
}
