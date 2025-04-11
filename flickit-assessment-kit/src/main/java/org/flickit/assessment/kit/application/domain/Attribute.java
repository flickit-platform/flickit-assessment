package org.flickit.assessment.kit.application.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.AttributeTranslation;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class Attribute {

    private final Long id;
    private final String code;
    private final String title;
    private final int index;
    private final String description;
    private final int weight;
    @Setter
    private Map<KitLanguage, AttributeTranslation> translations;
    @EqualsAndHashCode.Exclude private final LocalDateTime creationTime;
    @EqualsAndHashCode.Exclude private final LocalDateTime lastModificationTime;
    private final UUID createdBy;
    private final UUID lastModifiedBy;
}
