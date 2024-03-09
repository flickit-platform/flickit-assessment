package org.flickit.assessment.core.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class Subject {

    private final long id;
    private final UUID refNum;
    private final String title;
    private List<QualityAttribute> qualityAttributes;
}
