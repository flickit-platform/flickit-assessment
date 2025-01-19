package org.flickit.assessment.core.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AssessmentReportMetadata {

    private final String intro;
    private final String prosAndCons;
    private final String steps;
    private final String participants;
}
