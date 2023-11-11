package org.flickit.assessment.kit.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Attribute {

    private final String code;

    private final String title;

    private final String description;

    private final int index;

    private final String subjectCode;

    private final int weight;
}
