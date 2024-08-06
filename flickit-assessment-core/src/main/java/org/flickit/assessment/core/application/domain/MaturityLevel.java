package org.flickit.assessment.core.application.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static java.util.Comparator.comparingInt;

@Getter
@RequiredArgsConstructor
public class MaturityLevel {

    private final long id;
    private final String title;
    private final int index;
    private final int value;
    private final String description;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final List<LevelCompetence> levelCompetences;

    public static MaturityLevel middleLevel(List<MaturityLevel> maturityLevels) {
        var sortedMaturityLevels = maturityLevels.stream()
            .sorted(comparingInt(MaturityLevel::getIndex))
            .toList();
        return sortedMaturityLevels.get((sortedMaturityLevels.size() / 2));
    }
}
