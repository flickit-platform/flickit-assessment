package org.flickit.assessment.kit.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class MaturityLevel {

    private final long id;
    private final String code;
    private final String title;
    @Setter
    private final int index;
    private final int value;
    @Setter
    private final List<MaturityLevelCompetence> competences;
}
