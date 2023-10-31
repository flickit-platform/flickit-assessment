package org.flickit.assessment.core.application.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ConfidenceLevel {

    COMPLETELY_UNSURE,
    FAIRLY_UNSURE,
    SOMEWHAT_UNSURE,
    FAIRLY_SURE,
    COMPLETELY_SURE;

    public static ConfidenceLevel valueOfById(int id) {
        return Stream.of(ConfidenceLevel.values())
            .filter(x -> x.getId() == id)
            .findAny()
            .orElse(null);
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

    public int getId() {
        return ordinal() + 1;
    }

    @JsonIgnore
    public String getTitle() {
        return name().replace('_', ' ').toLowerCase();
    }
}
