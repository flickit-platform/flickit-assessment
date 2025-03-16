package org.flickit.assessment.core.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.space.SpaceStatus;

@Getter
@RequiredArgsConstructor
public class Space {

    private final long id;
    private final String title;
    private final SpaceStatus status;
}
