package org.flickit.assessment.kit.application.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.kit.ImageSize;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class KitBanner {

    private final long kitId;
    private final ImageSize size;
    private final String path;
}
