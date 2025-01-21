package org.flickit.assessment.core.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class Subject {

    private final long id;
    private final int index;
    private final String title;
    private final String description;
    private final int weight;
    private List<Attribute> attributes;
}
