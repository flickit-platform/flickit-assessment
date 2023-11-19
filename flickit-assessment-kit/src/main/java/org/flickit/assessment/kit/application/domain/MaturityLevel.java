package org.flickit.assessment.kit.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class MaturityLevel {

    private final long id;
    private final String code;
    private final String title;
    private final int index;
    private final int value;
    private final List<MaturityLevelCompetence> competences;
}
