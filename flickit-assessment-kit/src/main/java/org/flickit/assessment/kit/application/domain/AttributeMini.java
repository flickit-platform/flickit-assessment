package org.flickit.assessment.kit.application.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class AttributeMini {

    private final Long id;
    private final String title;
}
