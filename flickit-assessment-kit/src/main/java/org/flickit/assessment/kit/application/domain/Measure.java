package org.flickit.assessment.kit.application.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class Measure {

    private final Long id;
    private final String code;
    private final String title;
    private final int index;
    private final String description;

    @EqualsAndHashCode.Exclude
    private final LocalDateTime creationTime;

    @EqualsAndHashCode.Exclude
    private final LocalDateTime lastModificationTime;
}
