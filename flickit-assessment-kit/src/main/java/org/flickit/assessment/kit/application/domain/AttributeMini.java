package org.flickit.assessment.kit.application.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AttributeMini {

    private final Long id;
    private final String title;
}
