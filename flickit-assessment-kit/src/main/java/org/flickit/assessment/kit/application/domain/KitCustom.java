package org.flickit.assessment.kit.application.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class KitCustom {

    private final long id;
    private final long kitId;
    private final String title;
    private final KitCustomData customData;
    private final LocalDateTime creationTime;
    private final LocalDateTime lastModificationTime;
    private final UUID createdBy;
    private final UUID lastModifiedBy;
}
