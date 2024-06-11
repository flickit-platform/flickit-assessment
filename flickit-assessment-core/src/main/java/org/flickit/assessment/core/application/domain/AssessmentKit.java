package org.flickit.assessment.core.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class AssessmentKit {

    private final long id;
    private final String title;
    private final Long kitVersion;
    private final List<MaturityLevel> maturityLevels;
}
