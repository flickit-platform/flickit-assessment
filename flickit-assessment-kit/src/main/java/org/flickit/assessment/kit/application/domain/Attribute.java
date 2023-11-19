package org.flickit.assessment.kit.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class Attribute {

    private final long id;
    private final String code;
    private final String title;
    private final int index;
    private final String description;
    private final int weight;
    private final LocalDateTime creationTime;
    private final LocalDateTime lastModificationTime;

}
