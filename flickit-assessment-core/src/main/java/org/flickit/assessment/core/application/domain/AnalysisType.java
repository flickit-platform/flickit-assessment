package org.flickit.assessment.core.application.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum AnalysisType {

    CODE_QUALITY("Code Quality");

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

    private final String title;

    public String getCode() {
        return name();
    }
}
