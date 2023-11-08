package org.flickit.assessment.core.application.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ConfidenceLevel {

    COMPLETELY_UNSURE("Completely unsure"),
    FAIRLY_UNSURE("Fairly unsure"),
    SOMEWHAT_UNSURE("Somewhat unsure"),
    FAIRLY_SURE("Fairly sure"),
    COMPLETELY_SURE("Completely sure");

    private final String title;

    public static ConfidenceLevel valueOfById(Integer id) {
        return Stream.of(ConfidenceLevel.values())
            .filter(x -> Objects.equals(x.getId(), id))
            .findAny()
            .orElse(getDefault());
    }

    public static int getValidId(Integer id) {
        return ConfidenceLevel.isValidId(id) ? id : ConfidenceLevel.getDefault().getId();
    }

    private static boolean isValidId(int id) {
        return id > 0 && id < ConfidenceLevel.values().length + 1;
    }

    public static ConfidenceLevel getDefault() {
        return COMPLETELY_UNSURE;
    }

    public static ConfidenceLevel getMaxLevel() {
        return ConfidenceLevel.COMPLETELY_SURE;
    }

    public int getId() {
        return ordinal() + 1;
    }

    public String getTitle() {
        return this.title;
    }
}
