package org.flickit.assessment.core.application.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.MessageBundle;

import java.util.Objects;
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
        return MessageBundle.message("ConfidenceLevel_" + name());
    }
}
