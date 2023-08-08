package org.flickit.flickitassessmentcore.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class MaturityLevel {

    private final long id;
    private final int level;
    private final List<LevelCompetence> levelCompetences;
}
