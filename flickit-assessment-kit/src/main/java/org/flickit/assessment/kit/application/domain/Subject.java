package org.flickit.assessment.kit.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class Subject {

    private final long id;
    private final String code;
    private final String title;
    private final int index;
    private final String description;
    private final List<Attribute> attributes;
    private final LocalDateTime creationTime;
    private final LocalDateTime lastModificationTime;
}
