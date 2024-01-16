package org.flickit.assessment.kit.application.domain;

import kotlin.jvm.internal.PackageReference;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class Attribute {

    private final Long id;
    private final String code;
    private final String title;
    private final int index;
    private final String description;
    private final int weight;
    private final LocalDateTime creationTime;
    private final LocalDateTime lastModificationTime;
    private final UUID createdBy;
    private final UUID lastModifiedBy;
}
