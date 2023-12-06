package org.flickit.assessment.kit.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SubjectQuestionnaire {

    private final Long id;
    private final Long subjectId;
    private final Long questionnaireId;
}
