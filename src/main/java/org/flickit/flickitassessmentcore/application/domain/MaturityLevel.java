package org.flickit.flickitassessmentcore.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static java.util.Comparator.comparingInt;

@Getter
@RequiredArgsConstructor
public class MaturityLevel {

    private final long id;
    private final int level;
    private final List<LevelCompetence> levelCompetences;

    public static MaturityLevel middleLevel(List<MaturityLevel> maturityLevels) {
        var sortedMaturityLevels = maturityLevels.stream()
            .sorted(comparingInt(MaturityLevel::getLevel))
            .toList();
        return sortedMaturityLevels.get((sortedMaturityLevels.size() / 2));
    }
}
