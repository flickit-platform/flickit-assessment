package org.flickit.assessment.kit.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public class Level {

    private final String code;

    private final String title;

    private final String description;

    private final int index;

    private final Map<String, Integer> levelCompetence;

    private final int value;
}
