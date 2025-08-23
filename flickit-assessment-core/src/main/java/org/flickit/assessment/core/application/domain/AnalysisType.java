package org.flickit.assessment.core.application.domain;

import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

@Getter
public enum AnalysisType {

    CODE_QUALITY;

    public static AnalysisType valueOfById(Integer id) {
        return Stream.of(AnalysisType.values())
            .filter(x -> Objects.equals(x.getId(), id))
            .findFirst()
            .orElse(null);
    }

    public static boolean isValidId(int id) {
        return id > 0 && id < AnalysisType.values().length + 1;
    }

    public int getId() {
        return ordinal() + 1;
    }
}
