package org.flickit.assessment.kit.application.domain;

import lombok.*;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.QuestionnaireTranslation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Questionnaire {

    private final Long id;
    private final String code;
    private final String title;
    private final int index;
    private final String description;
    @Setter
    @EqualsAndHashCode.Exclude private List<Question> questions;
    @Setter
    @EqualsAndHashCode.Exclude private Map<KitLanguage, QuestionnaireTranslation> translations;
    @EqualsAndHashCode.Exclude private final LocalDateTime creationTime;
    @EqualsAndHashCode.Exclude private final LocalDateTime lastModificationTime;
}
