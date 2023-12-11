package org.flickit.assessment.kit.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class KitUser {

    private final Long id;
    private final Long userId;
    private final Long kitId;
}
