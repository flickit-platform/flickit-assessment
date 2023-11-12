package org.flickit.assessment.kit.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class Subject {

    @JsonIgnore
    private final Long id;

    private final String code;

    private final String title;

    private final String description;

    private final int index;

    private final int weight;

    private final List<String> questionnaireCodes;
}
