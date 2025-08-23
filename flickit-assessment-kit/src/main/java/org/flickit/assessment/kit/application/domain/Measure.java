package org.flickit.assessment.kit.application.domain;

import lombok.*;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.MeasureTranslation;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode
public class Measure {

    private final Long id;
    private final String code;
    private final String title;
    private final int index;
    private final String description;

    @Setter
    private Map<KitLanguage, MeasureTranslation> translations;

    @EqualsAndHashCode.Exclude
    private final LocalDateTime creationTime;

    @EqualsAndHashCode.Exclude
    private final LocalDateTime lastModificationTime;
}
