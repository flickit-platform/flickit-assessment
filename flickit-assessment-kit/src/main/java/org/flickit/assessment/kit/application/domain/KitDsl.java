package org.flickit.assessment.kit.application.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class KitDsl {

    private final Long id;
    private final String dslPath;
    private final String jsonPath;
    private final Long kitId;
    private final LocalDateTime creationTime;
}
