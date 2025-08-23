package org.flickit.assessment.advice.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class AssessmentResult {

    private final UUID id;

    private final long kitVersionId;

    private final UUID assessmentId;

    @Setter
    private KitLanguage language;
}
