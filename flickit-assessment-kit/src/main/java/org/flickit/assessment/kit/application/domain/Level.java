package org.flickit.assessment.kit.application.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class Level {

    @JsonIgnore
    private final Long id;

    private final String code;

    private final String title;

    private final String description;

    @Setter
    private int index;

    @Setter
    private Map<String, Integer> levelCompetence;

    private final int value;
}
