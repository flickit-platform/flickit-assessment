package org.flickit.assessment.kit.application.domain;

import lombok.*;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.MaturityLevelTranslation;

import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MaturityLevel {

    private final Long id;
    private final String code;
    private final String title;
    private final int index;
    private final String description;
    private final int value;
    @Setter
    private Map<KitLanguage, MaturityLevelTranslation> translations;
    @Setter
    private List<MaturityLevelCompetence> competences;
}
